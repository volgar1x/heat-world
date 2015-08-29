// Version 20150510034947
// Name    Create_items
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510034947_Create_items implements Migration {
    def up() {
        execute """
create table items(
    uid integer not null primary key,
    gid integer not null,
    effects bytea not null,
    position smallint not null,
    quantity integer not null
)
"""
    }

    def down() {
        execute "drop table items"
    }
}
