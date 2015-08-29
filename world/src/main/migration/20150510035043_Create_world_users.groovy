// Version 20150510035043
// Name    Create_world_users
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510035043_Create_world_users implements Migration {
    def up() {
        execute "create table world_users(id integer primary key)"
        execute "delete from items"
        execute "delete from players"
        execute "drop index players_userid_key"
        execute "alter table players add constraint players_userid_fk foreign key (userId) references world_users(id)"
    }

    def down() {
        execute "alter table players drop constraint players_userid_fk"
        execute "create index players_userid_key on players using hash (userId)"
        execute "drop table world_users"
    }
}
