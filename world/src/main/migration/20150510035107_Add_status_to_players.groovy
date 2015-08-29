// Version 20150510035107
// Name    Add_status_to_players
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510035107_Add_status_to_players implements Migration {
    def up() {
        execute """
alter table players add status smallint not null default 0,
                    add status_msg varchar(255)
"""
    }

    def down() {
        execute """
alter table players drop column status,
                    drop column status_msg
"""
    }
}
