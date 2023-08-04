package videogamedb.feeders

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CsvFeeder extends Simulation {

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

  val csvFeeder = csv("data/gameCsvFile.csv").circular //Para pasar datos desde un archivo .csv utilizamos el metodo csv("ruta de archivo") e indicamos como se obtendran dichos datos
  //.circular nos sirve para obtener los datos de forma repetitiva
  //.random nos sirve para  obtener los datos de forma aleatoria

  def getSpecifcVideoGame() = {
    repeat(10) {
      feed(csvFeeder) //Este metodo nos sirve para indicar el archivo .scv que utilizaremos
        .exec(http("Get video game with name - #{name}") //Para utilizar los parametros del archivo csv, lo hacemos mediante #{parametro}
        .get("/videogame/#{gameId}") //
        .check(jsonPath("$.name").is("#{name}"))
        .check(status.is(200)))
        .pause(1)
    }
  }

  val scn = scenario("Csv feeder test")
    .exec(getSpecifcVideoGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
