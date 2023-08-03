package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class AddPauseTime extends Simulation {

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

  // 2 Scenario
  val scn = scenario("Video Game DB - 3 calls")

    .exec(http("Get all video games - 1st call")
    .get("/videogame"))
    .pause(5) //Podemos agregar pausas directamente con valor. Por defecto es tomado en milisegundos

    .exec(http("Get specific game")
      .get("/videogame/1"))
    .pause(1, 10) //Tambien podemos manejar rangos entre varios valores. aqui pausara en un valor aleatorio entre 1 a 10 segundos

    .exec(http("Get all video games - 2nd call")
    .get("/videogame"))
    .pause(3000.milliseconds) //Tambien podremos indicar otras medidas de tiempo, en este caso indicamos que se pausara 3000 milisegundos
    //Para ello debemos usar la clase DurationInt

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
