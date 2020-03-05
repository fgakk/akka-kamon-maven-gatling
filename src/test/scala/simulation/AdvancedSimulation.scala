package simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import scala.concurrent.duration._

class AdvancedSimulation extends Simulation {

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://localhost:8080/api") // Here is the root for all relative URLs
    .acceptEncodingHeader("gzip, deflate")

  val users = scenario("Users").exec(ShowDetail.showDetail, Repeat.repeatedExecution, AddComment.addComment)

  setUp(
    users.inject(rampUsers(2000) during (10 seconds))
  ).protocols(httpProtocol)

  object ShowDetail {
    val showDetail = exec(http("List Articles")
        .get("/articles/")
      .check(jsonPath("$[0].headlineSlug").saveAs("headline")))
      .pause(2)
      .exec(http("Select one Article")
        .get("/articles/${headline}"))
      .pause(3)
  }

  object Repeat {
    val repeatedExecution = repeat(10, "n") {
      exec(http("Repeat action")
        .get("/articles/"))
        .pause(1)
    }
  }

  object AddComment {

    val feeder = jsonFile("comments.json").circular
    val addComment =
      exec(http("List Article")
        .get("/articles/")
        .check(jsonPath("$[0].id").saveAs("id")))
      .feed(feeder)
      .pause(1)
      .exec(http("Post")
        .post("/articles/${id}/comment")
          .body(StringBody("""{"author": "${author}", "text":"${text}"}""")).asJson
        .check(status.is(session => 200)))
  }
}




