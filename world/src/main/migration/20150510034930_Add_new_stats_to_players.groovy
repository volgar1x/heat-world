// Version 20150510034930
// Name    Add_new_stats_to_players
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510034930_Add_new_stats_to_players implements Migration {
    def up() {
        execute """
alter table players
    add life smallint not null,
    add energy smallint not null,
    add maxEnergy smallint not null,
    add actions smallint not null,
    add movements smallint not null,
    add prospecting smallint not null,
    add summonableCreatures smallint not null
"""
    }

    def down() {
        execute """
alter table players
    drop column life,
    drop column energy,
    drop column maxEnergy,
    drop column actions,
    drop column movements,
    drop column prospecting,
    drop column summonableCreatures
"""
    }
}
