// Version 20150510034901
// Name    Add_fields_to_players
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510034901_Add_fields_to_players implements Migration {
    def up() {
        execute """
alter table players
    add breedId smallint not null,
    add sex boolean not null,
    add lookId smallint not null,
    add headId smallint not null,
    add colors int[] not null,
    add mapId int not null,
    add cellId smallint not null,
    add directionId smallint not null,
    add experience double precision not null
"""
    }

    def down() {
        execute """
alter table players
    drop column breedId,
    drop column sex,
    drop column lookId,
    drop column headId,
    drop column colors,
    drop column mapId,
    drop column cellId,
    drop column directionId,
    drop column experience
"""
    }
}
