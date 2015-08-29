package org.heat.world.controllers.utils;

import org.heat.world.players.Player;
import org.rocket.network.props.PropPresence;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PropPresence(value = Player.class, presence = false)
public @interface PlayerChoosing { }
