package com.github.blackrush.flyroad

trait Migration {
    @Delegate groovy.sql.Sql sql

    abstract def up()
    abstract def down()
}
