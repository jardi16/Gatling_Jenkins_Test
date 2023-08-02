package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class CheckResponseCode extends Simulation {

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

  // 2 Scenario
  val scn = scenario("Video Game DB - 3 calls")

    .exec(http("Get all video games - 1st call")
      .get("/videogame")
      .check(status.is(200))) //.check() nos sirve para validar el statusCode de una respuesta
    .pause(5)

    .exec(http("Get specific game")
      .get("/videogame/1")
      .check(status.in(200 to 210))) //Tambien podremos validar el statuscode dentro de un rango
    .pause(1, 10)

    .exec(http("Get all video games - 2nd call")
      .get("/videogame")
      .check(status.not(404), status.not(500))) //Tambien podemos validar que no devuelva ciertos statuscode
    .pause(3000.milliseconds)

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
