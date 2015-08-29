// Version 20150510034912
// Name    Add_scale_to_players
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510034912_Add_scale_to_players implements Migration {
    def up() {
        execute "alter table players add scale smallint not null"
    }

    def down() {
        execute "alter table players drop column scale"
    }
}
