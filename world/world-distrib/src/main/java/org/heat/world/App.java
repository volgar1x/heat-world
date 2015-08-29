package org.heat.world;

import org.rocket.dist.RocketLauncher;

public final class App {
    private App() {}

    public static void main(String[] args) {
        RocketLauncher.takeOff(new StdWorldRocket());
    }
}
