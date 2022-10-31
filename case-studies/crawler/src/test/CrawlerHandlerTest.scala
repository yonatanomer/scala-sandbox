// todo

//import cats.effect.{IO, Resource}
//import cats.implicits._
//import cats.effect.{IO, Resource}
//import cats.effect.unsafe.implicits.global
//import cats.implicits._
//import com.dimafeng.testcontainers.{ForAllTestContainer, MockServerContainer}
//import org.http4s.{Headers, MediaType, Method, Request}
//import org.http4s.blaze.client.BlazeClientBuilder
//import org.http4s.client.Client
//import org.http4s.headers.Accept
//import org.scalatest.flatspec.AnyFlatSpec
//import org.scalatest.matchers.should.Matchers
//import test_util.MockServer
//
//// todo AsyncWordSpec
////https://www.lewuathe.com/async-spec-in-scalatest.html
//class CrawlerHandlerTest extends AnyFlatSpec with ForAllTestContainer with Matchers {
//  val container: MockServerContainer = MockServerContainer("5.14.0")
//
//  // start docker instance of the mock server
//  override def afterStart(): Unit = {
//
//    val mockServer = MockServer(container)
//    println(s"Mock server started on ${container.container.getEndpoint}: ${container.container.getServerPort}")
//  }
//
//  "crawler" should "correctly validate missing url params" in {
//    val expected = "java.lang.Throwable: Invalid value for: query parameter url (missing)"
//
//    val res =
//      for {
//        _ <- Crawler.startServer
//        client <- BlazeClientBuilder[IO].resource
//        response <- Resource.eval(MockServer.sendGetRequest(s"http://localhost:9999/crawl", client))
//      } yield response shouldBe expected
//
//    res.use(IO.pure).unsafeRunSync()
//  }
//
//}
