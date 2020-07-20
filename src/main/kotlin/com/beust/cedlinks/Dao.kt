package com.beust.cedlinks

import io.micronaut.http.HttpResponse
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import javax.inject.Singleton

@Singleton
class Dao {
    private val log = LoggerFactory.getLogger(Dao::class.java)

    fun insertLink(url: String, title: String, comment: String, imageUrl: String? = null) {
        transaction {
            Links.insert {
                it[Links.url] = url
                it[Links.title] = title
                it[Links.comment] = comment
                if (imageUrl != null) it[Links.imageUrl] = imageUrl
                it[Links.saved] = Dates.formatDate(LocalDateTime.now())
            }
        }
        if (listLinks().size >= 6) {
            publish(markPublished = true)
        }
    }

    fun listLinks(all: Boolean = false): List<LinkFromDb> {
        val result = arrayListOf<LinkFromDb>()
        transaction {
            if (all) {
                Links.selectAll().forEach {
                    result.add(LinkFromDb(it[Links.id],
                            it[Links.url], it[Links.title], it[Links.comment], it[Links.imageUrl],
                            saved = it[Links.saved], published = it[Links.published]))
                }
            } else {
                Links.select {
                    Links.published.isNull()
                }.forEach {
                    result.add(LinkFromDb(it[Links.id],
                            it[Links.url], it[Links.title], it[Links.comment], it[Links.imageUrl]))
                }
            }
        }
        return result
    }

    fun preview(): String {
        val content = linksToHtml(listLinks())
        return Template.render("preview.mustache", mapOf("content" to content))
    }

    fun linksToHtml(links: List<Link>): String {
        return Template.render("post.mustache", mapOf("links" to links))
    }

    fun publish(markPublished: Boolean, draft: Boolean = true): HttpResponse<String> {
        val links = listLinks()
        val ids = links.map { it.id }

        val result = try {
            Wordpress().postNewArticle(links, draft)
            if (markPublished) {
                transaction {
                    val date = Dates.formatDate(LocalDateTime.now())
                    Links.update({ Links.id.inList(ids) }) {
                        log.info("Updating ids to $date")
                        it[Links.published] = date
                    }
                }
            }
            HttpResponse.ok<String>()
        } catch(ex: Exception) {
            log.error("Error posting", ex)
            HttpResponse.serverError<String>().body(ex.message)
        }

        return result
    }
}