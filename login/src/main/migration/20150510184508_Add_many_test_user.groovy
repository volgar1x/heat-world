// Version 20150510184508
// Name    Add_many_test_user
// Author  Blackrush

import com.github.blackrush.flyroad.*

class Migration_20150510184508_Add_many_test_user implements Migration {
    def up() {
        10.times {
            def username = "test" + it
            def nickname = "User" + it
            execute """
            insert into users(username, nickname, communityId, secretQuestion, secretAnswer, rank, salt, hashpass) values (
                              $username, $nickname,   0,           'test',         'test',       10,
            'uyuxgjypiryfvpbplydfrypprpjugpgnqtvseuoevqccrrrvvuepnkkenosdjelvszlgwnuliqqsnoyddaqpkofosaueippzglafatzoacydespfuuwvzwhglzstfmxbdaxtaxbwrecfudwdhtalujarkryyusdwheoimrgapfomxkqgbtlmeddmkajewqqwujcxcccuhboneqguxztidjsztgvrliyhltrljujeyrtvwagjxqplscpaibvygpts',
            'f048f8d0a7f37ded02293e3f0fc651cf248f14022e4670ab349475d90efa3724df70a5446d9651dc9a7c0ce46b907bd0ffe7aa5538ec911920f9e233096a3491'
            )
            """
        }
    }

    def down() {
        execute "delete from users where nickname like 'User%'"
    }
}
