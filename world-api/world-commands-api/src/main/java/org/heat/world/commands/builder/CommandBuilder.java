package org.heat.world.commands.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.heat.world.commands.*;
import org.heat.world.commands.impl.AbstractCommand;
import org.heat.world.commands.impl.DefaultCommandTree;
import org.heat.world.commands.impl.SimpleCommandManager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandBuilder {
    @Getter
    private final String name;
    private String description;
    private CommandAction action;
    private final Map<String, CommandBuilder> subBuilders;
    private final List<String> aliases;

    private final Consumer<CommandTree> consumer;

    public CommandBuilder(String name, Consumer<CommandTree> consumer) {
        this.name = name;
        this.consumer = consumer;
        this.subBuilders = Maps.newHashMap();
        this.aliases = Lists.newArrayList();
    }

    public CommandTree build() {
        CommandTree cmd = build(createSyntax(true));
        consumer.accept(cmd);
        return cmd;
    }

    private CommandTree build(String syntax) {
        Command command = createCommand(action, syntax);
        Map<String, CommandTree> subCommandTrees = Maps.newHashMap();

        for (CommandBuilder sub : subBuilders.values())
            subCommandTrees.put(sub.getName(), sub.build());

        return new DefaultCommandTree(
                name,
                command,
                subCommandTrees);
    }

    private Command createCommand(CommandAction action, String syntax) {
        return new AbstractCommand(
                name,
                syntax,
                description.isEmpty()
                        ? "aucun description"
                        : description,
                aliases,
                action);
    }

    private String createSyntax(boolean main) {
        String base = "<b>" + (main ? SimpleCommandManager.prefix : "") + name + " </b>";

        String syntax = "";
        if(!subBuilders.isEmpty()) {
            syntax = subBuilders.values().stream()
                    .map((x) -> {
                        String sub = "-" + x.getName() + " " + x.createSyntax(false);
                        return main || x.subBuilders.size() > 1 ?  base + sub + "\n" : sub;
                    })
                    .collect(Collectors.joining());
        }
        else if(action != null && !action.getArguments().isEmpty()) {
            if(main) syntax = base;

            syntax += action.getArguments().values().stream()
                    .map((x) -> {
                        String label = "#" + x.getLabel();
                        String[] statics = x.getStaticArgs();

                        if(x.getStaticArgs().length > 0)
                            label += Arrays.stream(statics)
                                    .map(Function.identity())
                                    .collect(Collectors.joining("/", "(", ")"));

                        if(!x.isRequired())
                            label = "["+label+"]";

                        return label+" ";
                    })
                    .collect(Collectors.joining());
        }
        else if(main) syntax = base;


        return syntax;
    }

    public CommandBuilder describe(String description) {
        this.description = description;

        return this;
    }

    public CommandBuilder to(Class<? extends CommandAction> action) {
        checkAction(action.getName());

        try {
            this.action = action.newInstance();
        } catch (Exception e) {
            throw new CommandBuildingException(
                    "Can't build command '%s' trying to create a new instance of %s : %s",
                    name, action.getName(), e.getMessage());
        }

        return this;
    }

    public CommandBuilder withSub(String name) {
        checkArguments(name);

        subBuilders.put(name, new CommandBuilder(name, consumer));

        return this;
    }

    public CommandBuilder withSub(String name, Consumer<SubCommandBuilder> consumer) {
        checkArguments(name);

        SubCommandBuilder sub = new SubCommandBuilder(name, this.consumer);
        consumer.accept(sub);
        subBuilders.put(name, sub);

        return this;
    }

    public CommandBuilder withAlias(String alias) {
        aliases.add(alias);

        return this;
    }

    public CommandBuilder withAliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }

    private void checkArguments(String sub) {
        if (action != null && !action.getArguments().isEmpty())
            throw new CommandBuildingException("Can't build command '%s' trying to add subcommand %s." +
                    "Cause: you can't add sub-commands if there are already some arguments into this command.",
                    name, sub);
    }

    private void checkAction(String action) {
        if(this.action != null)
            throw new CommandBuildingException("Can't build command '%s' trying to add actionClass %s." +
                    "Cause: you can have just one actionClass per command",
                    name, action);
    }

    public static class SubCommandBuilder extends CommandBuilder{
        public SubCommandBuilder(String name, Consumer<CommandTree> consumer) {
            super(name, consumer);
        }

        /**
         * @return null, please build the parent builder
         */
        @Deprecated
        public CommandTree build() {
            return null;
        }
    }
 }