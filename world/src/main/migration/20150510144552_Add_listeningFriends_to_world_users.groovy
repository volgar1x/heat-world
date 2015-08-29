// Version 20150510144552
// Name    Add_listeningFriends_to_world_users
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510144552_Add_listeningFriends_to_world_users implements Migration {
    def up() {
        execute "alter table world_users add listeningFriends boolean not null default('f')"
    }

    def down() {
        execute "alter table world_users drop column listeningFriends"
    }
}
