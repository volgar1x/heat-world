package org.heat.world.guilds;

/**
 * You really should not rely on this class, like... ever.
 * @version 2.21
 */
@SuppressWarnings("unused")
public enum WorldGuildRanks {
    ON_TRIAL(0, 999),
    LEADER(1, 1),
    SECOND_IN_COMMAND(2, 2),
    TREASURER(3, 3),
    PROTECTOR(4, 4),
    CRAFTSMAN(5, 5),
    RESERVIST(6, 6),
    DOGSBODY(7, 200),
    GUARD(8, 7),
    SCOUT(9, 8),
    SPY(10, 9),
    DIPLOMAT(11, 10),
    SECRETARY(12, 11),
    PENITENT(13, 104),
    NUISANCE(14, 150),
    DESERTER(15, 110),
    TORTURER(16, 100),
    APPRENTICE(17, 98),
    MERCHANT(18, 97),
    BREEDER(19, 96),
    RECRUITING_OFFICER(20, 95),
    GUIDE(21, 93),
    MENTOR(22, 94),
    CHOSEN_ONE(23, 92),
    COUNSELLOR(24, 91),
    MUSE(25, 90),
    GOVERNOR(26, 89),
    MURDERER(27, 88),
    INITIATE(28, 87),
    THIEF(29, 86),
    TREASURE_HUNTER(30, 85),
    POACHER(31, 84),
    TRAITOR(32, 120),
    PET_KILLER(33, 82),
    MASCOT(34, 103),
    PERCEPTOR_KILLER(35, 105),
    ;

    public final short id, order;
    WorldGuildRanks(int id, int order) {
        this.id = (short) id;
        this.order = (short) order;
    }
}
