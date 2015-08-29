// Version 20150510210554
// Name    Create_notifs
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510210554_Create_notifs implements Migration {
    def up() {
        execute """
        create table player_notifs(
            player_id integer not null references players(id) on delete cascade,
            notif_id integer not null,
            primary key(player_id, notif_id)
        )
        """
    }

    def down() {
        execute "drop table player_notifs"
    }
}
