// Version 20150510035101
// Name    Create_guilds
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510035101_Create_guilds implements Migration {
    def up() {
        execute """
create table guilds(
    id integer primary key,
    name varchar(50) unique not null,
    emblem_foreground_id smallint not null,
    emblem_foreground_color integer not null,
    emblem_background_id smallint not null,
    emblem_background_color integer not null
)
"""
        execute """
create table guild_members(
    guild_id integer not null references guilds(id) on delete cascade,
    player_id integer not null references players(id) on delete cascade,
    permissions integer not null,
    rank smallint not null,
    given_experience_percent smallint not null,

    primary key(guild_id, player_id)
)
"""

    }

    def down() {
        execute "drop table guild_members"
        execute "drop table guilds"
    }
}
