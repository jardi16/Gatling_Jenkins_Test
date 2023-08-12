package videogamedb.finalsimulation

import io.gatling.http.Predef._
import io.gatling.core.Predef._

class JeffSimulationWithFeeders extends Simulation{

  val httpProtocol = http.baseUrl("https://www.videogamedb.uk")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val data = csv("data/gameCsvFile.csv").circular

  def authenticator() = {
    exec(
      http("authenticator")
        .post("/api/authenticate")
        .body(ElFileBody("bodies/authenticationRqBody.json")).asJson
        .check(jsonPath("$.token").saveAs("jwtToken"))
    )
  }

  def getAllGames() = {
    exec(
      http("get all games")
        .get("/api/videogame")
        .check(status.in(200 to 204))
        .check(status.not(400),status.not(500))
        .check(jsonPath("$[0].id").saveAs("getId"))
    )
  }

  def getGameById() = {
    exec(
      http("get game by id")
        .get("/api/videogame/#{getId}")
        .check(jsonPath("$.name").is("Gran Turismo 3"))
        .check(bodyString.saveAs("getGameResponseBody"))
    )//.exec{session => println(session("getGameResponseBody").as[String]);session}
  }

  def createNewGame() = {
    feed(data)
    .exec(
      http("create new game")
        .post("/api/videogame")
        .header("Authorization","Bearer #{jwtToken}")
        .body(ElFileBody("bodies/newGameTemplate.json")).asJson
        .check(status.in(200 to 204))
        .check(bodyString.saveAs("createResponseBody"))
    ).exec{session => println(session("createResponseBody").as[String]);session}
  }

  val scn = scenario("load with feeders")
    .exec(authenticator())
    .pause(3)
    .exec(getAllGames())
    .pause(3)
    .repeat(3){
      exec(getGameById())
    }.pause(3)
    .exec(createNewGame())

  setUp(
    scn.inject(
      //nothingFor(10),
      atOnceUsers(20),
      rampUsersPerSec(1).to(10).during(5)
    ).protocols(httpProtocol)
  )

}
