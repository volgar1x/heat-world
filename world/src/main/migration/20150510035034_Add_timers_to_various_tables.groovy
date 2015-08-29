// Version 20150510035034
// Name    Add_timers_to_various_tables
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510035034_Add_timers_to_various_tables implements Migration {
    def up() {
        execute "delete from items"
        execute "delete from players"
        execute """
alter table players
    add created_at timestamp not null default CURRENT_TIMESTAMP,
    add updated_at timestamp not null,
    add deleted_at timestamp,

    -- when does the player has been last used
    add last_used_at timestamp not null
"""
        execute """
alter table items
    add created_at timestamp not null default CURRENT_TIMESTAMP,
    add updated_at timestamp not null,
    add deleted_at timestamp
"""
        execute "alter table player_items add created_at timestamp not null default CURRENT_TIMESTAMP"
        execute "alter table player_shortcuts add created_at timestamp not null default CURRENT_TIMESTAMP"
    }

    def down() {
        execute """
alter table players
    drop column created_at,
    drop column updated_at,
    drop column deleted_at,
    drop column last_used_at
"""
        execute """
alter table items
    drop column created_at,
    drop column updated_at,
    drop column deleted_at
"""
        execute "alter table player_items drop column created_at"
        execute "alter table player_shortcuts drop column created_at"
    }
}
