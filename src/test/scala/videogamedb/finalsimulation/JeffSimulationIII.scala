package videogamedb.finalsimulation

import io.gatling.http.Predef._
import io.gatling.core.Predef._

class JeffSimulationIII extends Simulation{

  val httpProtocol = http.baseUrl("https://www.videogamedb.uk")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")


  def authentication() = {
    exec(
      http("jwt authentication")
        .post("/api/authenticate")
        .body(ElFileBody("bodies/authenticationRqBody.json")).asJson
        .check(jsonPath("$.token").saveAs("jwtToken"))
    )
  }
  def createNewGame() = {
      exec(
        http("create new game")
          .post("/api/videogame")
          .header("Authorization","Bearer #{jwtToken}")
          .body(ElFileBody("bodies/createGameBodyRq.json")).asJson
          .check(bodyString.saveAs("responseBody"))
      ).exec{session => println(session("responseBody").as[String]);session}
  }

  val scn = scenario("jeff simulation III")
    .exec(authentication())
    .pause(2)
    .exec(createNewGame())


  setUp(
    scn.inject(
      atOnceUsers(5)
    ).protocols(httpProtocol)
  )
}
