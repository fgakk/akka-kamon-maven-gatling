package simulation

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class BasicSimulation extends Simulation {

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://localhost:8080/api") // Here is the root for all relative URLs
    .acceptEncodingHeader("gzip, deflate")

  val scn: ScenarioBuilder = scenario("ArticleRestServiceSimulation")
    .exec(http("articles")
        .get("/articles/")
    )
    .pause(1)

  setUp(
    scn.inject(atOnceUsers(100))
      .protocols(httpProtocol)
  )

}
