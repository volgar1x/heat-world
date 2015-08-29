package org.heat.world.controllers;

import com.ankamagames.dofus.network.enums.GameContextEnum;
import com.ankamagames.dofus.network.enums.ShortcutBarEnum;
import com.ankamagames.dofus.network.messages.game.shortcut.*;
import com.ankamagames.dofus.network.types.game.shortcut.ShortcutObjectItem;
import com.ankamagames.dofus.network.types.game.shortcut.ShortcutSpell;
import com.github.blackrush.acara.Listen;
import org.heat.world.controllers.events.NewContextEvent;
import org.heat.world.controllers.utils.Basics;
import org.heat.world.controllers.utils.Idling;
import org.heat.world.items.WorldItem;
import org.heat.world.players.Player;
import org.heat.world.players.metrics.PlayerSpell;
import org.heat.world.players.shortcuts.ItemShortcut;
import org.heat.world.players.shortcuts.PlayerShortcut;
import org.heat.world.players.shortcuts.PlayerShortcutBar;
import org.heat.world.players.shortcuts.SpellShortcut;
import org.rocket.network.Controller;
import org.rocket.network.NetworkClient;
import org.rocket.network.Prop;
import org.rocket.network.Receive;

import javax.inject.Inject;

@Controller
@Idling
public class ShortcutsController {
    @Inject NetworkClient client;
    @Inject Prop<Player> player;

    @Listen
    public void showContent(NewContextEvent evt) {
        if (evt.getContext() != GameContextEnum.ROLE_PLAY) return;

        PlayerShortcutBar bar = player.get().getShortcutBar();

        client.transaction(tx -> {
            for (ShortcutBarEnum barType : ShortcutBarEnum.values()) {
                tx.write(new ShortcutBarContentMessage(barType.value,
                        bar.getShortcutsOf(barType).map(PlayerShortcut::toShortcut)));
            }
        });
    }

    @Receive
    public void add(ShortcutBarAddRequestMessage msg) {
        Player player = this.player.get();
        PlayerShortcutBar bar = player.getShortcutBar();

        ShortcutBarEnum barType = ShortcutBarEnum.valueOf(msg.barType).get();

        final PlayerShortcut shortcut;
        if (msg.shortcut instanceof ShortcutObjectItem) {
            ShortcutObjectItem s = (ShortcutObjectItem) msg.shortcut;
            WorldItem item = player.getWallet().findByUid(s.itemUID).get();
            shortcut = new ItemShortcut(player.getId(), s.slot, item.getUid(), item.getGid());
        } else if (msg.shortcut instanceof ShortcutSpell) {
            ShortcutSpell s = (ShortcutSpell) msg.shortcut;
            PlayerSpell spell = player.getSpells().findById(s.spellId).get();
            shortcut = new SpellShortcut(player.getId(), s.slot, (short) spell.getId());
        } else {
            // just ignore unsupported shortcuts
            // TODO: log unsupported features
            client.write(Basics.noop());
            return;
        }

        bar.add(shortcut);

        client.transaction(tx -> {
            tx.write(new ShortcutBarRefreshMessage(barType.value, shortcut.toShortcut()));
            tx.write(Basics.noop());
        });
    }

    @Receive
    public void remove(ShortcutBarRemoveRequestMessage msg) {
        Player player = this.player.get();
        PlayerShortcutBar bar = player.getShortcutBar();

        ShortcutBarEnum barType = ShortcutBarEnum.valueOf(msg.barType).get();
        int slot = msg.slot;

        if (bar.remove(barType, slot)) {
            client.write(new ShortcutBarRemovedMessage(barType.value, slot));
        } else {
            // TODO: shortcut remove error reason
            client.write(new ShortcutBarRemoveErrorMessage((byte) 0));
        }
    }

    @Receive
    public void swap(ShortcutBarSwapRequestMessage msg) {
        Player player = this.player.get();
        PlayerShortcutBar bar = player.getShortcutBar();

        ShortcutBarEnum barType = ShortcutBarEnum.valueOf(msg.barType).get();
        int from = msg.firstSlot;
        int to = msg.secondSlot;

        bar.swap(barType, from, to)
                .ifLeft(shortcut ->
                    client.transaction(tx -> {
                        tx.write(new ShortcutBarRemovedMessage(barType.value, from));
                        tx.write(new ShortcutBarRefreshMessage(barType.value, shortcut.toShortcut()));
                        tx.write(Basics.noop());
                    })
                )
                .ifRight(pair ->
                    client.transaction(tx -> {
                        tx.write(new ShortcutBarRefreshMessage(barType.value, pair.first.toShortcut()));
                        tx.write(new ShortcutBarRefreshMessage(barType.value, pair.second.toShortcut()));
                        tx.write(Basics.noop());
                    })
                );
    }
}
