// Version 20150510034937
// Name    Add_spells_to_players
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510034937_Add_spells_to_players implements Migration {
    def up() {
        execute "delete from players"
        execute "create type player_spell as (id int, level int, position int)"
        execute "alter table players add spells player_spell[] not null"
    }

    def down() {
        execute "drop type player_spell"
        execute "alter table players drop column spells"
    }
}
