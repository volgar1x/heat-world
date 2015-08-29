// Version 20150510035005
// Name    Add_kamas_to_players
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510035005_Add_kamas_to_players implements Migration {
    def up() {
        execute "delete from players"
        execute "alter table players add kamas integer not null"
    }

    def down() {
        execute "drop table players drop column kamas"
    }
}
