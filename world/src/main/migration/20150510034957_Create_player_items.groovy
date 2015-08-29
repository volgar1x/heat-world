// Version 20150510034957
// Name    Create_player_items
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510034957_Create_player_items implements Migration {
    def up() {
        execute """
create table player_items(
    player_id integer not null references players(id) on delete restrict,
    item_uid integer not null references items(uid) on delete cascade,
    primary key(player_id, item_uid)
)
"""
    }

    def down() {
        execute "drop table player_items"
    }
}
