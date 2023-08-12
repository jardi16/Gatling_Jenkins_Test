package videogamedb.finalsimulation

import io.gatling.http.Predef._
import io.gatling.core.Predef._

class FinalAll extends Simulation{

  val httpProtocol = http.baseUrl("https://www.videogamedb.uk")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val data = csv("data/gameCsvFile.csv").circular

  val USERS = System.getProperty("USERS","10").toInt
  val USERSPERSEC = System.getProperty("USERSPERSEC","10").toInt
  val TESTTIME = System.getProperty("TESTTIME","20").toInt

  def authenticator() = {
    exec(
      http("jwt token")
        .post("/api/authenticate")
        .body(ElFileBody("bodies/authenticationRqBody.json")).asJson
        .check(status.in(200 to 204))
        .check(jsonPath("$.token").saveAs("jwtToken"))
    )
  }

  def createGame() = {
    feed(data)
    .exec(
      http("create new game")
        .post("/api/videogame")
        .header("Authorization", "Bearer #{jwtToken}")
        .body(ElFileBody("bodies/newGameTemplate.json")).asJson
        .check(status.in(200 to 204))
        .check(bodyString.saveAs("createResponseBody"))
    ).exec{session => println(session("createResponseBody").as[String]);session}
  }

  def updateGame() = {
    exec(
      http("update game")
        .put("/api/videogame/1")
        .header("Authorization","Bearer #{jwtToken}")
        .body(ElFileBody("bodies/updateGameTemplate.json")).asJson
        .check(status.in(200 to 204))
        .check(status.not(400),status.not(500))
        .check(bodyString.saveAs("updateResponseBody"))
    ).exec{session => println(session("updateResponseBody").as[String]);session}
  }

  val scn = scenario("load final Jeff All")
    .exec(authenticator())
    .pause(3)
    .exec(createGame())
    .exec(updateGame())

  setUp(
    scn.inject(
      nothingFor(5),
      atOnceUsers(USERS),
      constantUsersPerSec(USERSPERSEC).during(TESTTIME)
    ).protocols(httpProtocol)
  )
}
