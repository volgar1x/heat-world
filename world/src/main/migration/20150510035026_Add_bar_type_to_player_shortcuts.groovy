// Version 20150510035026
// Name    Add_bar_type_to_player_shortcuts
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510035026_Add_bar_type_to_player_shortcuts implements Migration {
    def up() {
        execute "delete from player_shortcuts"
        execute """
alter table player_shortcuts
    add column bar_type smallint not null,
    drop constraint player_shortcuts_pkey,
    add primary key (player_id, slot, bar_type)
"""
    }

    def down() {
        execute """
alter table player_shortcuts
    drop column bar_type,
    drop constraint player_shortcuts_pkey,
    add primary key (player_id, slot)
"""
    }
}
