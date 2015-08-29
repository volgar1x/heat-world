package org.heat.world.items;

public interface WorldWallet extends WorldBag {
    int getKamas();
    void setKamas(int kamas);

    default void plusKamas(int kamas) {
        setKamas(getKamas() + kamas);
    }
}
