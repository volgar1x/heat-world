package org.heat.login;

import org.rocket.dist.RocketLauncher;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        RocketLauncher.takeOff(new StdLoginRocket());
    }
}
