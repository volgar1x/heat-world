// Version 20150510035015
// Name    Create_player_shortcuts
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510035015_Create_player_shortcuts implements Migration {
    def up() {
        execute """
create table player_shortcuts(
    player_id integer references players(id) on delete cascade,
    slot integer not null,
    type varchar(10) not null check (type IN ('item', 'spell')),
    item_uid integer references items(uid) on delete cascade,
    spell_id integer,
    primary key (player_id, slot)
)
"""
    }

    def down() {
        execute "drop table player_shortcuts"
    }
}
