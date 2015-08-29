// Version 20150510141048
// Name    Create_contacts
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510141048_Create_contacts implements Migration {
    def up() {
        execute """
create table contacts(
  from_id integer not null references world_users(id) on delete cascade,
  to_id integer not null references world_users(id) on delete cascade,
  kind varchar not null check (kind in ('friend', 'ignored')),
  primary key(from_id, to_id)
)
"""
        execute "create index on contacts using hash (kind)"
    }

    def down() {
        execute "drop table contacts"
    }
}
