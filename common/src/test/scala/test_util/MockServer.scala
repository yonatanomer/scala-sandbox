package test_util

import cats.effect.IO
import com.dimafeng.testcontainers.MockServerContainer
import org.http4s.client.Client
import org.http4s.headers.Accept
import org.http4s.{Headers, MediaType, Method, Request}
import org.mockserver.client.MockServerClient
import org.mockserver.mock.Expectation
import org.mockserver.model.Header.header
import org.mockserver.model.{HttpRequest, HttpResponse}
import cats.implicits._

class MockServer(mockServerClient: MockServerClient) {

  // TODO params, body, raw text body, expectd content type

  def mockResponse(
      method: String,
      path: String,
      responseBody: String,
      statusCode: Int
  ): Array[Expectation] = {
    mockServerClient.reset()

    val request = HttpRequest.request
      .withPath(path)
      .withMethod(method)

    val response = HttpResponse.response
      .withHeaders(header("Content-Type", "application/json; charset=utf-8"))
      .withBody(responseBody)
      .withStatusCode(statusCode)

    mockServerClient.when(request).respond(response)
  }

}

object MockServer {
  def apply(container: MockServerContainer): MockServer =
    new MockServer(
      new MockServerClient(
        container.container.getHost,
        container.container.getServerPort
      )
    )

  def sendGetRequest(url: String, client: Client[IO]): IO[String] = {
    val request =
      Request[IO](
        method = Method.GET,
        uri = org.http4s.Uri.unsafeFromString(url),
        headers = Headers(Accept(MediaType.application.json))
      )

    client
      .expectOr[String](request)(err => err.as[String].map(new Throwable(_)))
      .map { response =>
        println(s"response from $url: $response")
        response
      }
      .recover { case e =>
        println(s"request to $url failed with $e")
        e.toString
      }
  }
}
