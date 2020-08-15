package com.beust.cedlinks

import io.micronaut.runtime.Micronaut
import javax.persistence.*

fun initDb() {
    val dbUrl = Config.jdbcUrl
    org.jetbrains.exposed.sql.Database.connect(dbUrl, driver = "org.postgresql.Driver",
            user = Config.jdbcUser, password = Config.jdbcPassword)
    DbMigration().execute()
}

@Entity
@Table(name = "links")
class LinkFromDb(
        @Id
        @SequenceGenerator(
                name = "LinksSequence",
                sequenceName = "links_id_seq",
                allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LinksSequence")
        var id: Long? = 0,

        @Column(name = "url")
        var url: String? = null,

        @Column(name = "title")
        var title: String? = null,

        @Column(name = "comment")
        var comment: String? = null,

        @Column(name = "imageUrl")
        var imageUrl: String? = null,

        @Column(name = "saved")
        var saved: String? = null,

        @Column(name = "published")
        var published: String? = null) {

    override fun toString() = "{LinkFromDb id: $id, url: $url, title: $title, comment: $comment"
}

fun main(args: Array<String>) {
//    initHibernate()
    initDb()
    Micronaut.build()
        .args(*args)
        .packages("com.beust.cedlinks")
        .start()
}

//fun main(args: Array<String>) {
//    initDb()
//    CedLinksApp().run(*args)
//}
