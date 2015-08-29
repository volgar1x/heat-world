package org.heat.world.controllers.utils;

import org.heat.world.users.WorldUser;
import org.rocket.network.props.PropPresence;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PropPresence(value = WorldUser.class)
public @interface Authenticated { }
