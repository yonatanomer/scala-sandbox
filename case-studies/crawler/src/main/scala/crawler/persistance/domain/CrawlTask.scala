package crawler.persistance.domain

case class CrawlTask(id: Long, url: String, pattern: String, depth: Option[Int])
