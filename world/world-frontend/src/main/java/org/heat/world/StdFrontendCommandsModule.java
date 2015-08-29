package org.heat.world;

import org.heat.world.commands.actions.AnnounceAction;
import org.heat.world.commands.actions.CreateGuildAction;
import org.heat.world.commands.actions.HelpAction;
import org.heat.world.commands.actions.TeleportAction;

/**
 * Managed by romain on 08/03/2015.
 */
public class StdFrontendCommandsModule extends StdCommandsModule {
    @Override
    protected void createCommands() {
        createNewCommand("help").describe("Liste et décrit vos commandes disponibles.")
                                .to(HelpAction.class)
                                .build();

        createNewCommand("announce").describe("Annonce à tous les joueurs un message.")
                                    .to(AnnounceAction.class)
                                    .build();

        createNewCommand("guild-create").describe("Create a new guild")
                                        .to(CreateGuildAction.class)
                                        .build();

        createNewCommand("teleport").describe("Téléporte à une map et cell donnée")
                                    .to(TeleportAction.class)
                                    .build();
    }
}
