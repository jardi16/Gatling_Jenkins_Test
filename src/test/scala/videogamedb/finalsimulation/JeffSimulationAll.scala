package videogamedb.finalsimulation

import io.gatling.http.Predef._
import io.gatling.core.Predef._

class JeffSimulationAll extends Simulation {

  val httpProtocol = http.baseUrl("https://www.videogamedb.uk")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val data = csv("data/gameCsvFile.csv").circular

  def USERS = System.getProperty("USERS","10").toInt

  def authenticator() = {
    exec (
      http("get jwt token")
        .post("/api/authenticate")
        .body(ElFileBody("bodies/authenticationRqBody.json")).asJson
        .check(jsonPath("$.token").saveAs("jwtToken"))
    )
  }

  def createVideoGame() = {
      feed(data)
      .exec(
        http("create video game")
          .post("/api/videogame")
          .header("Authorization","Bearer #{jwtToken}")
          .body(ElFileBody("bodies/newGameTemplate.json")).asJson
          .check(status.in(200 to 204))
          .check(status.not(400),status.not(500))
          .check(bodyString.saveAs("createResponseBody"))
      )//.exec{session => println(session("createResponseBody").as[String]);session}
  }

  def getAllGames() = {
      exec(
        http("get all games")
          .get("/api/videogame")
          .check(status.is(200))
          .check(status.not(400))
          .check(jsonPath("$[2].id").saveAs("getId"))
      )
  }

  def getGameById() = {
      exec(
        http("get game by id #{getId}")
          .get("/api/videogame/#{getId}")
          .check(status.is(200))
          .check(jsonPath("$.name").is("Tetris"))
      )
  }

  val scn = scenario("final load simulation")
    .exec(authenticator())
    .pause(5)
    .exec(createVideoGame())
    .pause(3)
    .exec(getAllGames())
    .exec(getGameById())

  setUp(
    scn.inject(
      nothingFor(5),
      atOnceUsers(USERS),
      constantUsersPerSec(10).during(20)
    ).protocols(httpProtocol)
  )

}
