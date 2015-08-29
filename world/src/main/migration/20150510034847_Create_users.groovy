// Version 20150510034847
// Name    Create_users
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510034847_Create_users implements Migration {
    def up() {
        execute "create table players(id integer primary key, userId integer not null, name varchar(30) not null unique)"
        execute "create index players_userId_key ON players (userId)"
    }

    def down() {
        execute "drop index players_userId_key"
        execute "drop table players"
    }
}
