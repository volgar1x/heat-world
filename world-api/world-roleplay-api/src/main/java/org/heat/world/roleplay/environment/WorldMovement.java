package org.heat.world.roleplay.environment;

import lombok.Getter;
import lombok.Value;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.fungsi.concurrent.Promise;
import org.fungsi.concurrent.Promises;
import org.heat.world.roleplay.WorldAction;
import org.heat.world.roleplay.WorldActionCanceledException;
import org.heat.world.roleplay.WorldActor;

import static lombok.AccessLevel.NONE;

@Value
public final class WorldMovement implements WorldAction {
    final WorldActor actor;
    final WorldMapPath path;

    @Getter(NONE) final Promise<WorldAction> end = Promises.create();

    @Override
    public Future<WorldAction> getEndFuture() {
        return end;
    }

    @Override
    public Future<WorldAction> cancel() {
        end.fail(new WorldActionCanceledException());
        return Futures.success(this);
    }

    public void notifyEnd() {
        end.complete(this);
    }

    public void notifyCancellation(WorldMapPoint point) {
        end.fail(new WorldMovementCanceledException(point));
    }
}
