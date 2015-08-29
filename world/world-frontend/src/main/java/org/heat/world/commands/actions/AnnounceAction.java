package org.heat.world.commands.actions;

import com.ankamagames.dofus.network.enums.TextInformationTypeEnum;
import com.google.inject.Inject;
import org.heat.UserRank;
import org.heat.world.commands.CommandSender;
import org.heat.world.commands.CommandValidation;
import org.heat.world.commands.StdCommandValidations;
import org.heat.world.commands.builder.ArgsBuilder;
import org.heat.world.commands.impl.AbstractCommandAction;
import org.heat.world.players.Player;
import org.heat.world.players.PlayerRegistry;
import org.heat.world.players.events.NoticePlayerEvent;

/**
 * Managed by romain on 18/03/2015.
 */
public class AnnounceAction extends AbstractCommandAction {
    @Inject PlayerRegistry players;

    @Override
    protected ArgsBuilder getArguments(ArgsBuilder b) {
        return b.addRequiredSpacedArg("message");
    }

    @Override
    public CommandValidation getValidation() {
        return StdCommandValidations.hasRank(UserRank.ANNOUNCER);
    }

    public void execute(CommandSender sender, boolean console, String[] args) {

        String name;

        if(sender instanceof Player) {
            name = "(Staff) " + ((Player) sender).getClickerName();
        } else
            name = "<b>Serveur</b>";

        String message = name + ": " + args[0];
        players.getEventBus().publish(new NoticePlayerEvent(
                        (byte) 0,
                        TextInformationTypeEnum.TEXT_INFORMATION_MESSAGE,
                        message)
        );
    }
}
