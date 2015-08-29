package org.heat.world.commands.actions;

import org.heat.world.commands.CommandSender;
import org.heat.world.commands.CommandValidation;
import org.heat.world.commands.builder.ArgsBuilder;
import org.heat.world.commands.impl.AbstractCommandAction;
import org.heat.world.guilds.events.GuildCreationEvent;
import org.heat.world.players.Player;

public class CreateGuildAction extends AbstractCommandAction {
    @Override
    protected ArgsBuilder getArguments(ArgsBuilder b) {return b;}

    @Override
    public CommandValidation getValidation() {
        return (sender, errors) -> {
            if (sender instanceof Player) {
                errors.unless(((Player) sender).getGuild() != null,
                              "You already have a guild");
            }
        };
    }

    @Override
    public void execute(CommandSender sender, boolean console, String[] args) {
        sender.getEventBus().publish(GuildCreationEvent.i);
    }
}
