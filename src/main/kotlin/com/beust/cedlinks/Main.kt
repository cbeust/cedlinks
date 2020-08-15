package com.beust.cedlinks

import io.micronaut.context.BeanContext
import io.micronaut.runtime.Micronaut.build
import javax.persistence.*


fun initDb() {
    val dbUrl = Config.jdbcUrl
    org.jetbrains.exposed.sql.Database.connect(dbUrl, driver = "org.postgresql.Driver",
            user = Config.jdbcUser, password = Config.jdbcPassword)
    DbMigration().execute()
}

@Entity
@Table(name = "links")
class HLink {
    @Id
    @SequenceGenerator(
            name = "LinksSequence",
            sequenceName = "links_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LinksSequence")
    var id: Long? = 0

    @Column(name = "url")
    lateinit var url: String

    @Column(name = "title")
    lateinit var title: String

    @Column(name = "comment")
    var comment: String? = null

    @Column(name = "imageUrl")
    var imageUrl: String? = null

    @Column(name = "saved")
    var saved: String? = null

    @Column(name = "published")
    var published: String? = null
}

fun main(args: Array<String>) {
//    initHibernate()
    initDb()
    build()
        .args(*args)
        .packages("com.beust.cedlinks")
        .start()
}

//fun main(args: Array<String>) {
//    initDb()
//    CedLinksApp().run(*args)
//}
