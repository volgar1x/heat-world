// Version 20150510034921
// Name    Add_stats_to_players
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510034921_Add_stats_to_players implements Migration {
    def up() {
        execute """
alter table players
    add column statsPoints smallint not null,
    add column spellsPoints smallint not null,
    add column strength smallint not null,
    add column vitality smallint not null,
    add column wisdom smallint not null,
    add column chance smallint not null,
    add column agility smallint not null,
    add column intelligence smallint not null
"""
    }

    def down() {
        execute """
alter table players
    drop column statsPoints,
    drop column spellsPoints,
    drop column strength,
    drop column vitality,
    drop column wisdom,
    drop column chance,
    drop column agility,
    drop column intelligence 
"""
    }
}
