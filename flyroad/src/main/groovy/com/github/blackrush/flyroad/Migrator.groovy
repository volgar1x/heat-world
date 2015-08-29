package com.github.blackrush.flyroad

args = args as List
def (way, url, user, pass) = args.take(4)
def migrationClasses = args.drop(4).sort().collect { Class.forName(it) }
if (way == 'down') {
    migrationClasses.reverse(true)
}

def sql = groovy.sql.Sql.newInstance(url, user, pass, "org.postgresql.Driver")

migrationClasses.each { cls ->
    def (_, version, name) = (cls.simpleName =~ /Migration_(\d+)_(.+)/)[0]
    def m = cls.newInstance()
    m.sql = sql
    sql.withTransaction {
        if (way == "up") {
            println "Migrate '${name.replace('_', ' ')}'"
            m.up()
            sql.execute "insert into schema_version(id, name) values($version, $name)"
        } else {
            println "Rollback '${name.replace('_', ' ')}'"
            m.down()
            sql.execute "delete from schema_version where id=$version"
        }
    }
}

