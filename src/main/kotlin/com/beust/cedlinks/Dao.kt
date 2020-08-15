package com.beust.cedlinks

import io.micronaut.http.HttpResponse
import org.slf4j.LoggerFactory
import java.net.URI
import java.time.LocalDateTime
import javax.inject.Singleton
import kotlin.reflect.KClass

open class Link(val url: String, val title: String, val comment: String?, val imageUrl: String?)

interface IDao {
    fun listLinks(all: Boolean = false): List<LinkFromDb>
    fun updatePublished(ids: List<Long>)
    fun submitPodcast(time: String?): String
    fun rss(): String
    fun insertPodcast(url: String, title: String)
}

abstract class DaoBase: IDao {
    private val log = LoggerFactory.getLogger(DaoBase::class.java)

    fun linksToHtml(links: List<LinkFromDb>): String {
        return Template.render("post.mustache", mapOf("links" to links))
    }

    protected fun publish(markPublished: Boolean, draft: Boolean = true): HttpResponse<String> {
        val links = listLinks()
        val ids: List<Long> = links.mapNotNull { it.id }

        return try {
            Wordpress().postNewArticle(links, draft)
            if (markPublished) {
                updatePublished(ids)
            }
            HttpResponse.ok<String>()
        } catch(ex: Exception) {
            log.error("Error posting", ex)
            HttpResponse.serverError<String>().body(ex.message)
        }
    }
}

@Singleton
class Dao2 constructor(private val emp: EntityManagerProvider) : DaoBase() {
    private fun <T : Any> query(query: String, cls: KClass<T>, parameter: Pair<String, Any>? = null): List<T> {
        emp.get().createEntityManager().let { em ->
            em.transaction.begin()
            val query = em.createQuery(query, cls.java)
            if (parameter != null) {
                query.setParameter(parameter.first, parameter.second)
            }
            val result = query.resultList
            em.transaction.commit()
            em.close()
            return result
        }
    }

    override fun listLinks(all: Boolean): List<LinkFromDb> {
        val result = if (all) query("FROM LinkFromDb", LinkFromDb::class)
        else query("FROM LinkFromDb WHERE published = null", LinkFromDb::class)
        return result
    }

    override fun updatePublished(ids: List<Long>) {
        emp.get().createEntityManager().let { em ->
            em.transaction.begin()
            val query = em.createQuery("SELECT l FROM LinkFromDb WHERE l.id in :ids", LinkFromDb::class.java)
            query.setParameter("ids", ids)
            val links = query.resultList
            val date = Dates.formatDate(LocalDateTime.now())
            links.forEach {
                it.published = date
                em.persist(it)
            }
            em.transaction.commit()
            em.close()
        }
    }

    override fun submitPodcast(time: String?): String {
        TODO("Not yet implemented")
    }

    override fun rss(): String {
        TODO("Not yet implemented")
    }

    override fun insertPodcast(url: String, title: String) {
        TODO("Not yet implemented")
    }

    fun preview(): String {
        val content = linksToHtml(listLinks())
        return Template.render("preview.mustache", mapOf("content" to content))
    }

    fun insertLink(url: String, title: String, comment: String, imageUrl: String? = null): HttpResponse<String> {
        emp.get().createEntityManager().let { em ->
            em.transaction.begin()
            em.persist(LinkFromDb(0, url, title, comment, imageUrl))
            em.transaction.commit()
            em.close()

            val redirectUrl = if (listLinks().size >= 6) {
                publish(markPublished = true)
                "https://beust.com/weblog/wp-admin/edit.php"
            } else {
                url
            }
            return HttpResponse.redirect<String>(URI(redirectUrl))
        }
    }
}

//@Singleton
//class Dao: DaoBase() {
//    private val log = LoggerFactory.getLogger(Dao::class.java)
//
//    fun insertLink(url: String, title: String, comment: String, imageUrl: String? = null): HttpResponse<String> {
//        transaction {
//            Links.insert {
//                it[Links.url] = url
//                it[Links.title] = title
//                it[Links.comment] = comment
//                if (imageUrl != null) it[Links.imageUrl] = imageUrl
//                it[saved] = Dates.formatDate(LocalDateTime.now())
//            }
//        }
//        val redirectUrl = if (listLinks().size >= 6) {
//            publish(markPublished = true)
//            "https://beust.com/weblog/wp-admin/edit.php"
//        } else {
//            url
//        }
//        return HttpResponse.redirect<String>(URI(redirectUrl))
//    }
//
//    override fun updatePublished(ids: List<Long>) {
////        transaction {
////            val date = Dates.formatDate(LocalDateTime.now())
////            Links.update({ Links.id.inList(ids) }) {
////                log.info("Updating ids to $date")
////                it[published] = date
////            }
////        }
//    }
//    override fun listLinks(all: Boolean): List<LinkFromDb> {
//        val result = arrayListOf<LinkFromDb>()
//        transaction {
//            if (all) {
//                Links.selectAll().forEach {
//                    result.add(LinkFromDb(it[Links.id],
//                            it[Links.url], it[Links.title], it[Links.comment], it[Links.imageUrl],
//                            saved = it[Links.saved], published = it[Links.published]))
//                }
//            } else {
//                Links.select {
//                    Links.published.isNull()
//                }.forEach {
//                    result.add(LinkFromDb(it[Links.id],
//                            it[Links.url], it[Links.title], it[Links.comment], it[Links.imageUrl]))
//                }
//            }
//        }
//        return result.map { LinkFromDb(it.id, it.url, it.title, it.comment, it.imageUrl) }
//    }
//
//    fun preview(): String {
//        val content = linksToHtml(listLinks())
//        return Template.render("preview.mustache", mapOf("content" to content))
//    }
//
//    fun linksToHtml(links: List<Link>): String {
//        return Template.render("post.mustache", mapOf("links" to links))
//    }
//
//    fun insertPodcast(url: String, title: String) {
//        transaction {
//            Podcasts.insert {
//                it[Podcasts.url] = url
//                it[Podcasts.title] = title
//                it[saved] = Dates.formatDate(LocalDateTime.now())
//            }
//            log.info("Inserted new podcast $url - $title")
//        }
//    }
//
//    fun submitPodcast(time: String?): String = Template.render("submitPodcast.mustache",
//            mapOf("time" to time))
//
//    fun rss(): String {
//        val podcasts = arrayListOf<Rss.Item>()
//        transaction {
//            Podcasts.selectAll()
//                    .orderBy(Podcasts.saved to SortOrder.DESC)
//                    .limit(10)
//                    .sortedBy { Podcasts.saved }
//                    .forEach {
//                podcasts.add(Rss.Item(it[Podcasts.title], it[Podcasts.url], it[Podcasts.saved]))
//            }
//        }
//        return Template.render("rss.mustache", Rss.Feed(Dates.formatDate(LocalDateTime.now()), podcasts))
//    }
//
//}