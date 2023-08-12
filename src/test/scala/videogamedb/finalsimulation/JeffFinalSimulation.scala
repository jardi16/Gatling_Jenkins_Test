package videogamedb.finalsimulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class JeffFinalSimulation extends Simulation{

  val httpProtocol = http.baseUrl("https://www.videogamedb.uk/")
    .acceptHeader("application/json")

  def getAllVideoGames() = {
    exec(
      http("Get all video games")
        .get("/api/videogame")
    ).pause(2)
  }

  val scn = scenario("Final load simulate")
    .exec(getAllVideoGames())

  setUp(
    scn.inject(
      nothingFor(5),
      //atOnceUsers(30), //Agrega todos los usuarios de golpe
      //constantUsersPerSec(10).during(10), //Carga estable durante el tiempo determinado
      rampUsers(100).during(10) //Carga ascendente durante el tiempo determinado. Reparte los usuarios indicados durante el timpo indicado
      //rampUsersPerSec(1).to(100).during(10) //Carga ascendente durante el tiempo determinado. Aumenta el numero de usuario por segundo
    ).protocols(httpProtocol)
  )
}
