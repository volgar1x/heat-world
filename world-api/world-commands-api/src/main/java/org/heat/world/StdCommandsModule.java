package org.heat.world;

import com.google.inject.AbstractModule;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;
import org.heat.world.commands.CommandManager;
import org.heat.world.commands.CommandTree;
import org.heat.world.commands.builder.CommandBuilder;
import org.heat.world.commands.impl.SimpleCommandManager;

/**
 * Managed by romain on 08/03/2015.
 */
public abstract class StdCommandsModule extends AbstractModule{
    protected MapBinder<String, CommandTree> commands;
    @Override
    protected void configure() {
        bind(CommandManager.class).to(SimpleCommandManager.class).asEagerSingleton();
        commands = MapBinder.newMapBinder(binder(), String.class, CommandTree.class);

        createCommands();
    }

    protected abstract void createCommands();

    protected CommandBuilder createNewCommand(String name) {
        LinkedBindingBuilder<CommandTree> binding = commands.addBinding(name);
        return new CommandBuilder(name, binding::toInstance);
    }
}
