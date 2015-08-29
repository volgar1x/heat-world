package org.heat.world.trading.impl.player;

import com.github.blackrush.acara.EventBus;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.heat.world.items.*;
import org.heat.world.trading.WorldTradeSide;
import org.heat.world.trading.impl.player.events.*;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.heat.world.trading.WorldTradeSide.FIRST;
import static org.heat.world.trading.WorldTradeSide.SECOND;

final class PlayerTradeImpl implements PlayerTrade {
    @Getter final EventBus eventBus;

    final ImmutableMap<WorldTradeSide, SideState> states;
    final Object[] globalTradeLock = new Object[0];
    int step = 0;

    Optional<? extends Result> result = Optional.empty();

    PlayerTradeImpl(EventBus eventBus, PlayerTrader first, PlayerTrader second) {
        this.eventBus = eventBus;
        this.states = ImmutableMap.of(
            FIRST,  new SideState(first),
            SECOND, new SideState(second)
        );
    }

    @Override
    public Optional<? extends Result> conclude() {
        if (!result.isPresent()) {
            SideState first = state(FIRST),
                    second = state(SECOND);

            if (!first.check || !second.check) {
                return Optional.empty();
            }

            synchronized (globalTradeLock) {
                this.result = Optional.of(new ConcludedResult(first.asUnmodifiableView(), second.asUnmodifiableView()));
            }

            eventBus.publish(new PlayerTradeConcludeEvent(this, result.get()))
                    .respond(e -> eventBus.publish(new PlayerTradeEndEvent(this)));
        }

        return result;
    }

    @Override
    public void cancel(WorldTradeSide side) {
        if (result.isPresent()) {
            return;
        }

        SideState state = state(side);
        PlayerTrader canceller = state.trader;

        synchronized (globalTradeLock) {
            result = Optional.of(new CancelledResult(canceller));
        }

        eventBus.publish(new PlayerTradeEndEvent(this));
    }

    @Override
    public boolean isConcluded() {
        return result.isPresent() && result.get() instanceof ConcludedResult;
    }

    @Override
    public boolean isCancelled() {
        return result.isPresent() && result.get() instanceof CancelledResult;
    }

    @Override
    public int getStep() {
        synchronized (globalTradeLock) {
            return step;
        }
    }

    @Override
    public void increaseStep() {
        synchronized (globalTradeLock) {
            step++;
        }
    }

    SideState state(WorldTradeSide side) {
        if (result.isPresent()) {
            throw new IllegalStateException("trade has been " + result.get());
        }
        return states.get(side);
    }

    //<editor-fold desc="Results">

    @RequiredArgsConstructor
    @Getter
    class ConcludedResult implements Result {
        final WorldItemWallet first, second;

        @Override
        public String toString() {
            return "concluded";
        }
    }

    @RequiredArgsConstructor
    class CancelledResult implements Result {
        final PlayerTrader canceller;

        @Override
        public WorldItemWallet getFirst() {
            throw new UnsupportedOperationException("trade has been cancelled by " + canceller);
        }

        @Override
        public WorldItemWallet getSecond() {
            throw new UnsupportedOperationException("trade has been cancelled by " + canceller);
        }

        @Override
        public String toString() {
            return "cancelled by " + canceller;
        }
    }

    //</editor-fold>
    //<editor-fold desc="SideState">

    @RequiredArgsConstructor
    final class SideState extends DelegateItemBag implements WorldItemWallet {
        final PlayerTrader trader;

        boolean check;
        MapItemBag items = MapItemBag.newHashMapItemBag();
        int kamas;

        WorldItemWallet asUnmodifiableView() {
            return WorldItemWallets.unmodifiable(items.getItemStream().collect(Collectors.toList()), kamas);
        }

        @Override
        protected WorldItemBag delegate() {
            return items;
        }

        @Override
        public int getKamas() {
            return kamas;
        }

        void check() {
            synchronized (globalTradeLock) {
                if (check) {
                    return;
                }

                this.check = true;
            }

            eventBus.publish(new PlayerTradeCheckEvent(PlayerTradeImpl.this, trader, true));
        }

        void uncheck() {
            synchronized (globalTradeLock) {
                if (!check) {
                    return;
                }

                this.check = false;
            }

            eventBus.publish(new PlayerTradeCheckEvent(PlayerTradeImpl.this, trader, false));
        }

        @Override
        public void setKamas(int kamas) {
            if (kamas < 0) {
                throw new IllegalArgumentException("you cannot set a negative amount of kamas");
            }
            synchronized (globalTradeLock) {
                this.kamas = kamas;
            }
            eventBus.publish(new PlayerTraderKamasEvent(PlayerTradeImpl.this, trader, this));
        }

        @Override
        public void add(WorldItem item) {
            synchronized (globalTradeLock) {
                super.add(item);
            }
            eventBus.publish(new PlayerTraderAddItemEvent(PlayerTradeImpl.this, trader, this, item));
        }

        @Override
        public void update(WorldItem item) {
            synchronized (globalTradeLock) {
                super.update(item);
            }
            eventBus.publish(new PlayerTraderUpdateItemEvent(PlayerTradeImpl.this, trader, this, item));
        }

        @Override
        public void remove(WorldItem item) {
            synchronized (globalTradeLock) {
                super.remove(item);
            }
            eventBus.publish(new PlayerTraderRemoveItemEvent(PlayerTradeImpl.this, trader, this, item));
        }

        @Override
        public Optional<WorldItem> tryRemove(int uid) {
            final Optional<WorldItem> option;
            synchronized (globalTradeLock) {
                option = super.tryRemove(uid);
            }
            option.ifPresent(item -> eventBus.publish(
                    new PlayerTraderUpdateItemEvent(PlayerTradeImpl.this, trader, this, item)));
            return option;
        }
    }
    //</editor-fold>
    //<editor-fold desc="PlayerTrade handy helpers">

    @Override
    public void check(WorldTradeSide side) {
        synchronized (globalTradeLock) {
            state(side).check();
        }
    }

    @Override
    public void uncheck(WorldTradeSide side) {
        synchronized (globalTradeLock) {
            state(side).uncheck();
        }
    }

    @Override
    public void uncheckAllIfNeeded() {
        states.forEach((side, state) -> {
            if (state.check) {
                state.uncheck();
            }
        });
    }

    @Override
    public PlayerTrader getTrader(WorldTradeSide side) {
        return states.get(side).trader;
    }

    @Override
    public WorldItemWallet getTradeBag(WorldTradeSide side) {
        return state(side);
    }

    //</editor-fold>
}
