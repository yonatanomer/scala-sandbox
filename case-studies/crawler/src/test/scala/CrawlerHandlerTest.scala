import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import com.dimafeng.testcontainers.{ForAllTestContainer, MockServerContainer}
import crawler.CrawlerCakePattern
import org.http4s.blaze.client.BlazeClientBuilder
import org.scalatest
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import test_util.MockServer

// todo AsyncWordSpec
//https://www.lewuathe.com/async-spec-in-scalatest.html
class CrawlerHandlerTest extends AnyFlatSpec with ForAllTestContainer with Matchers {
  val container: MockServerContainer = MockServerContainer("5.14.0")

  // start docker instance of the mock server
  override def afterStart(): Unit = {

    val mockServer = MockServer(container)
    println(s"Mock server started on ${container.container.getEndpoint}: ${container.container.getServerPort}")
  }

  "crawler" should "correctly validate missing url params" in {
    val expected = "java.lang.Throwable: Invalid value for: query parameter url (missing)"

    val res: Resource[IO, scalatest.Assertion] =
      for {
        _ <- CrawlerCakePattern.startServer
        client <- BlazeClientBuilder[IO].resource
        response <- Resource.eval(MockServer.sendGetRequest(s"http://localhost:9999/crawl", client))
      } yield response shouldBe expected

    res.use(IO.pure).unsafeRunSync()
  }

}
