package org.heat.world.commands.actions;

import org.heat.UserRank;
import org.heat.world.commands.CommandSender;
import org.heat.world.commands.CommandValidation;
import org.heat.world.commands.StdCommandValidations;
import org.heat.world.commands.builder.ArgsBuilder;
import org.heat.world.commands.impl.AbstractCommandAction;
import org.heat.world.players.events.PlayerTeleportEvent;
import org.heat.world.roleplay.environment.WorldMapPoint;

/**
 * Managed by romain on 05/05/2015.
 */
public class TeleportAction extends AbstractCommandAction {
    @Override
    protected ArgsBuilder getArguments(ArgsBuilder b) {
        return b.addRequiredArg("mapId")
                .addArg("cellId");
    }

    @Override
    public CommandValidation getValidation() {
        return StdCommandValidations.hasRank(UserRank.ANIMATOR);
    }

    @Override
    public void execute(CommandSender sender, boolean console, String[] args) {
        int cellId;
        
        if(args.length > 1)
            cellId = Integer.parseInt(args[1]);
        else
            cellId = 100; //TODO: teleport to random available cell
            
        sender.getEventBus().publish(new PlayerTeleportEvent(
                Integer.parseInt(args[0]),
                WorldMapPoint.of(cellId).get()
        ));
    }
}