package org.heat;

public enum UserRank {
    BOTTOM,
    USER,
    PREMIUM,
    POWER_USER,
    SUPPORT,
    ANNOUNCER,
    ANIMATOR,
    MODERATOR,
    GAME_MASTER,
    ADMIN,
    GOD,
    TOP,
    ;

    public boolean enough(UserRank rank) {
        return this != BOTTOM &&
              this.ordinal() >= rank.ordinal();
    }
}
