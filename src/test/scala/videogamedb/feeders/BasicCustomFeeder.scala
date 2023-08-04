package videogamedb.feeders

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class  BasicCustomFeeder extends Simulation {

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

  //Tenemos la posibilidad de pasar ciertos parametros sin utilizar archivos .csv
  //En este caso crearemos un id generado con un iterador
  var idNumbers = (1 to 10).iterator

  //Teniendo el iterador creado, asignamos cada id a un gameId dentro de un map recorriendo el iterador con el metodo .next()
  val customFeeder = Iterator.continually(Map("gameId" -> idNumbers.next()))

  def getSpecificVideoGame() = {
    repeat(10) {
      feed(customFeeder)
        .exec(http("Get video game with id - #{gameId}") //Dentro del escenario asignamos los id mediante su llave
        .get("/videogame/#{gameId}")
        .check(status.is(200)))
        .pause(1)
    }
  }

  val scn = scenario("Basic Custom Feeder")
    .exec(getSpecificVideoGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
