package com.beust.cedlinks

import io.micronaut.runtime.Micronaut.*
import io.quarkus.runtime.Quarkus
import io.quarkus.runtime.QuarkusApplication

fun initDb() {
    val dbUrl = Config.jdbcUrl
    org.jetbrains.exposed.sql.Database.connect(dbUrl, driver = "org.postgresql.Driver",
            user = Config.jdbcUser, password = Config.jdbcPassword)
    DbMigration().execute()
}

fun main(args: Array<String>) {
    Quarkus.run(*args)
}

//class Main: QuarkusApplication {
//    override fun run(vararg args: String?): Int {
//        TODO("Not yet implemented")
//    }
//
//}

//fun main(args: Array<String>) {
//    initDb()
//    build()
//        .args(*args)
//        .packages("com.beust.cedlinks")
//        .start()
//}

//fun main(args: Array<String>) {
//    initDb()
//    CedLinksApp().run(*args)
//}
