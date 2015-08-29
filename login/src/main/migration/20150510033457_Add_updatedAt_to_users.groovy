// Version 20150510033457
// Name    Add_updatedAt_to_users
// Author  Blackrush
import com.github.blackrush.flyroad.*

class Migration_20150510033457_Add_updatedAt_to_users implements Migration {
    def up() {
        execute "alter table users add updatedAt timestamp not null default(now())"
    }

    def down() {
        execute "alter table users drop column updatedAt"
    }
}
