// Version 20150510035052
// Name    Add_channels_to_world_users
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510035052_Add_channels_to_world_users implements Migration {
    def up() {
        execute "delete from items"
        execute "delete from players"
        execute "delete from world_users"
        execute "alter table world_users add channels integer not null"
    }

    def down() {
        execute "alter table world_users drop column channels"
    }
}
