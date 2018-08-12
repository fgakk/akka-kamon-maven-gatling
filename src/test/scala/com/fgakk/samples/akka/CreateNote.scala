package com.fgakk.samples.akka

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class CreateNote extends Simulation {

  val httpConf: HttpProtocolBuilder = http
    .baseURL("http://localhost:8080") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")


  val action: ChainBuilder =
    exec(http("Create a note")
      .post("/notes")
      .formParam("value", "test notes"))
      .pause(500.millis)
    repeat(50, "getnotes") {
      exec(http("Get notes")
        .get("/notes"))
    }

  val scn: ScenarioBuilder = scenario("Create and get notes").exec(action)

  setUp(
    scn
      .inject(rampUsers(2000) over 20.seconds)
      .protocols(httpConf)
  )
}

