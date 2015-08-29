package org.heat.world.commands.impl;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.heat.world.commands.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Managed by romain on 08/03/2015.
 */
public class SimpleCommandManager implements CommandManager {
    private final Map<String, CommandTree> commandTrees;

    public final static char prefix = '!';

    @Inject
    public SimpleCommandManager(Map<String, CommandTree> commands,
                                Injector injector) {
        ImmutableMap.Builder<String, CommandTree> builder = ImmutableMap.builder();
        builder.putAll(commands);

        for(CommandTree tree: commands.values()) {
            injectTree(injector, tree);

            for(String alias: tree.getCommand().getAliases())
               builder.put(alias, tree);
        }

        this.commandTrees = builder.build();
    }

    public boolean execute(CommandSender sender, String msg, boolean console) {
        if(!console && (msg.length() < 2 || msg.charAt(0) != prefix))
            return false;

        String[] split = (msg = msg.substring(
                console
                ? 0 : 1
        )).toLowerCase().split(" ");

        String name = split[0];

        CommandTree command = findCommand(sender, name, console);

        if(command == null)
            return true;
        else if(!playerFulfillsConditions(command, sender, console))
            return true;


        try {
            parseAndExecuteCommand(sender, command, msg, console);
        } catch(IllegalArgumentException e) {
            sender.reply(true, console, "Commande erronée", "veillez à respecter la syntaxe: \n" +
                    command.getCommand().getSyntax());
        }
        return true;
    }

    /**
     * @param command Command name without prefix
     * @param args Command parameters
     */
    public void forward(CommandSender sender, String command, String... args) {
        String commandFull = args.length > 0
                ? Arrays.stream(args)
                    .map(Function.identity())
                    .collect(Collectors.joining(command + " ", " ", ""))
                : command;

        execute(sender, commandFull, false);
    }

    /**
     * @param commandFull The command without prefix & his parameters as String
     */
    public void forward(CommandSender sender, String commandFull) {
        execute(sender, commandFull, false);
    }

    private void parseAndExecuteCommand(CommandSender sender, CommandTree command, String msg, boolean console) {
        String[] split = msg.split(" ");
        String input = split[0];

        if(command.getName().equals(input))
            msg = msg.substring(command.getName().length());
        else
            msg = msg.substring(input.length());

        if(msg.startsWith(" ")) msg = msg.substring(1);

        String[] params = split(msg, " ");

        if(!commandHasValidParameters(command, params)) {
            sender.reply(true, console, "Commande incomplète", "veillez à respecter la syntaxe: \n" +
                    command.getCommand().getSyntax());
            return;
        }

        if(params.length > 0 && command.getSubCommandTrees().size() > 0) {
            String next = params[0];

            CommandTree subTree = command.getSubCommandTrees().get(next);

            if (subTree == null && (subTree = getSubCommandByShortcut(command, next)) == null) {
                sender.reply(true, console, "La sous-commande n'existe pas: \n" +
                        command.getCommand().getSyntax());
                return;
            }

            parseAndExecuteCommand(sender, subTree, msg.startsWith(" ") ? msg.substring(1) : msg, console);
            return;
        }

        CommandArg[] commandArgs = command.getCommand().getAction().getArguments().values().toArray(new CommandArg[0]);
        int length = commandArgs.length, index = length - 1;

        if(params.length > index && length > 0 && commandArgs[index].isWithSpaces()) {
            int middle = msg.indexOf(params[index]);

            String[] args = msg.substring(0, middle-(middle == 0 ? 0:1)).split(" ");
            String arg = msg.substring(middle);

            char capital = Character.toUpperCase(arg.charAt(0));
            String[] lastArg = new String[] { (capital + arg.substring(1)) };

            if(args.length > 1)
                params = Stream.concat(Arrays.stream(args), Arrays.stream(lastArg)).toArray(String[]::new);
            else
                params = lastArg;
        }

        command.getCommand().getAction().execute(sender, console, params);
    }

    private String[] split(String arg, String delimiter) {
        String[] split = arg.split(delimiter);
        return split.length == 1 && split[0].isEmpty() ? new String[0] : split;
    }

    private CommandTree findCommand(CommandSender sender, String name, boolean console) {
        CommandTree command;

        if((command = commandTrees.get(name)) == null
                && (command = getCommandByShortcut(sender, name, console)) == null)
            return null;

        return command;
    }

    private CommandTree getSubCommandByShortcut(CommandTree command, String shortcut) {
        CommandTree subCommand = null;

        for(String name: command.getSubCommandTrees().keySet()) {
            if (name.startsWith(shortcut)) {
                if(subCommand != null) return null;

                subCommand = command.getSubCommandTrees().get(name);
            }
        }

        return subCommand;
    }

    private boolean playerFulfillsConditions(CommandTree command, CommandSender sender, boolean console) {
        CommandValidation.Errors errors = CommandErrors.newErrors();

        command.getCommand().getAction().getValidation().
                        validate(sender, errors);

        if(!errors.isEmpty()) {
            sender.reply(true,
                    console, String.format("vous ne remplissez pas les conditions nécessaire: \n%s", errors.toString()));
            return false;
        }

        return true;
    }

    /**
     * @param command is the command the to check.
     * @param split represents parameters.
     * @return false if there isn't the same number of arguments between
     *         ones needed & ones written by the sender. This method return
     *         false again if the current command hasn't any action and has
     *         some sub commandTrees and if the sender haven't written any parameter.
     *         Return false if static args are wrong.
     */
    private boolean commandHasValidParameters(CommandTree command, String[] split) {
        int count = command.getCommand().getAction().getRequiredArguments().size();
        int size = split.length;


        if(size < count
                || (size < 1 && command.getSubCommandTrees().size() > 0 &&
                command.getCommand().getAction() == null))
            return false;

        CommandArg[] args = command.getCommand().getAction().getArguments().values().toArray(new CommandArg[0]);

        for(int i=0; i<args.length && i<size; i++) {
            CommandArg arg = args[i];
            String[] statics = arg.getStaticArgs();

            if(statics.length > 0) {
                boolean checked = false;

                for(String sarg: statics)
                    if(sarg.equals(split[i]))
                        checked = true;

                if(!checked) return false;
            }
        }

        return true;
    }

    private CommandTree getCommandByShortcut(CommandSender sender, String shortcut, boolean console) {
        List<String> found = commandTrees.keySet().stream().filter(
                (name) -> name.startsWith(shortcut)
        ).collect(Collectors.toList());

        int cSize = found.size();

        if(cSize == 1) return commandTrees.get(found.get(0));

        if(cSize <= 0) {
            sender.reply(true, console, "Commande non reconnue", "!help pour la liste.");
        } else {
            sender.reply(true, console, "vous cherchez peut-être: " +
                    found.stream()
                            .map(Function.identity())
                            .collect(Collectors.joining(", ")));
        }

        return null;
    }

    private void injectTree(Injector injector, CommandTree tree) {
        CommandAction action = tree.getCommand().getAction();

        if(action != null)
            injector.injectMembers(action);

        for(CommandTree sub: tree.getSubCommandTrees().values())
                injectTree(injector, sub);
    }
}
