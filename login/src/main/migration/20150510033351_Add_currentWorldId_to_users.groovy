// Version 20150510033351
// Name    Add_currentWorldId_to_users
// Author  Blackrush
import com.github.blackrush.flyroad.*

class Migration_20150510033351_Add_currentWorldId_to_users implements Migration {
    def up() {
        execute "alter table users add currentWorldId integer"
    }

    def down() {
        execute "alter table users drop column currentWorldId"
    }
}
