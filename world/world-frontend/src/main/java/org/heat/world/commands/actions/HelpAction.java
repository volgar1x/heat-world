package org.heat.world.commands.actions;

import com.google.inject.Inject;
import org.heat.world.commands.*;
import org.heat.world.commands.builder.ArgsBuilder;
import org.heat.world.commands.impl.AbstractCommandAction;
import org.heat.world.commands.impl.CommandErrors;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Managed by romain on 08/03/2015.
 */
public class HelpAction extends AbstractCommandAction {
    @Inject Map<String, CommandTree> commands;

    @Override
    protected ArgsBuilder getArguments(ArgsBuilder b) {
        return b.addArg("commande");
    }

    @Override
    public CommandValidation getValidation() {
        return StdCommandValidations.empty();
    }

    public void execute(CommandSender sender, boolean console, String[] args) {
        boolean spot = args.length > 0;

        if(spot) {
            String name = args[0];
            CommandTree tree = commands.get(name);

            //works with/without prefix
            if(tree != null || (tree = commands.get(name.substring(1))) != null) {
                Command command = tree.getCommand();

                sender.reply(false, console, "\nSyntaxe de votre commande: \n\n" +
                        command.getSyntax() + "\n> " +
                        command.getDescription());
            } else
                sender.reply(true, console, "Commande non trouvée.");
        } else {
            sender.reply(false, console, "\nListe des commandes disponibles:\n\n" + commands.values()
                    .stream()
                    .map((x) -> {
                        Command command = x.getCommand();

                        CommandValidation.Errors errors = CommandErrors.newErrors();
                        x.getCommand().getAction().getValidation().validate(sender, errors);

                        boolean b = errors.isEmpty();

                        return b ? command.getSyntax() + "\n> " +
                                   command.getDescription()
                                : "";
                    })
                    .collect(Collectors.joining("\n")));
        }
    }
}
