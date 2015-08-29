package org.heat.world.controllers.utils;

import org.heat.world.roleplay.WorldAction;
import org.rocket.network.props.PropPresence;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@RolePlaying
@PropPresence(value = WorldAction.class, presence = false)
public @interface Idling { }
