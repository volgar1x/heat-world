// Version 20150510033048
// Name    Create_users
// Author  Blackrush
import com.github.blackrush.flyroad.*

class Migration_20150510033048_Create_users implements Migration {
    def up() {
        execute """
create table users(
    id serial primary key,
    username varchar(50) unique not null,
    nickname varchar(50) unique not null,
    salt char(256) not null,
    hashpass char(128) not null, -- SHA-512
    communityId smallint not null,
    secretQuestion varchar(250) not null,
    secretAnswer varchar(250) not null,
    rank integer not null,
    subscriptionEnd timestamp default(null), -- nullable
    banEnd timestamp default(null), -- nullable
    connected boolean not null default('no'),
    createdAt timestamp not null default(now())
)
"""
    }

    def down() {
        execute "drop table users"
    }
}
