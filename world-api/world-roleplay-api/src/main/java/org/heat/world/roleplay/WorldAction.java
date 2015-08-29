package org.heat.world.roleplay;

import org.fungsi.concurrent.Future;

/**
 * A {@link org.heat.world.roleplay.WorldAction} is bound to an {@link org.heat.world.roleplay.WorldActor},
 * has an end and might be cancelled (depending on implementation details).
 */
public interface WorldAction {
    /**
     * Get action's {@link org.heat.world.roleplay.WorldActor}
     * @return a non-null actor
     */
    WorldActor getActor();

    /**
     * Get action's end future
     * @return a non-null future
     */
    Future<WorldAction> getEndFuture();

    /**
     * Request to cancel action. Returned future must be the same result of {@link #getEndFuture()}
     * @return a non-null future
     */
    Future<WorldAction> cancel();
}
