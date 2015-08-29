// Version 20150510033208
// Name    Add_lastServerId_to_users
// Author  Blackrush
import com.github.blackrush.flyroad.*

class Migration_20150510033208_Add_lastServerId_to_users implements Migration {
    def up() {
        execute "alter table users add lastServerId integer"
    }

    def down() {
        execute "alter table users drop column lastServerId"
    }
}
