// Version 20150510141105
// Name    Add_fields_to_world_users
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510141105_Add_fields_to_world_users implements Migration {
    def up() {
        execute "delete from world_users"
        execute """
        alter table world_users add nickname varchar not null,
                                add lastconnection timestamp not null
        """
        execute "create index on world_users using hash (nickname)"
    }

    def down() {
        execute "drop index world_users_nickname_idx"
        execute """
        alter table world_users drop column nickname
                                drop column lastconnection
        """
    }
}
