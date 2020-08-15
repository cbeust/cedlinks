package com.beust.cedlinks

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import javax.inject.Provider
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

@Factory
@Singleton
class EntityManagerProvider {
    @Bean
    @Singleton
    fun get() = instance

    private val instance = init()

    private fun init(): EntityManagerFactory {
        val properties = mapOf(
            "hibernate.connection.url" to Config.jdbcUrl,
            "hibernate.connection.username" to Config.jdbcUser,
            "hibernate.connection.password" to Config.jdbcPassword
        )
        return Persistence.createEntityManagerFactory("org.hibernate.tutorial.jpa", properties)
    }
}