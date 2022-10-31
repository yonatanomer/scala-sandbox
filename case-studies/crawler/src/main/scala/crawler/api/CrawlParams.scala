package crawler.api

import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

case class CrawlParams(url: String, pattern: String, depth: Option[Int])

object CrawlParamsOps extends App {

//  implicit class CrawlParamsJsonOps(params: CrawlParams) {
//
//    def toJson(): String = params.asJson.noSpaces
//    def validate()
//  }

  //val jsn = CrawlParams("x", "y", None).toJson()

//  println("object to json: " + jsn)
//  println("string to object from json:" + decode[CrawlParams](jsn))

}
