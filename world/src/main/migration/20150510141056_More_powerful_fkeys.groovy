// Version 20150510141056
// Name    More_powerful_fkeys
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510141056_More_powerful_fkeys implements Migration {
    def up() {
        execute "alter table players drop constraint players_userid_fk"
        execute """
        alter table players
            add constraint players_userid_fk
            foreign key (userid)
            references world_users(id)
            on delete cascade
        """
        execute "alter table player_items drop constraint player_items_player_id_fkey"
        execute """
        alter table player_items
            add constraint player_items_player_id_fkey
            foreign key (player_id)
            references players(id)
            on delete cascade
        """
    }

    def down() {
       
    }
}
