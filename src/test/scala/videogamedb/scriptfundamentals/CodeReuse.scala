package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CodeReuse extends Simulation {

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

  def getAllVideoGames() = { //Metodo que nos permite encapsular el codigo referente a un escenario
    repeat(3) { //Metodo usado para repetir n cantidad de veces un escenario. Los metodos del escenario se ponen dentro del repeat
      exec(http("Get all video games")
        .get("/videogame")
        .check(status.is(200)))
    }
  }

  def getSpecificGame() = {
    repeat(5, "counter") { //Tambien podemos ingresar una variable para que se asigne el valor correspondiente en cada recorrido del  loop
      exec(http("Get specific game with id: #{counter}")
        .get("/videogame/#{counter}")
        .check(status.in(200 to 210)))
    }
  }

  val scn = scenario("Code resuse")
    .exec(getAllVideoGames()) //Invocacion de metodos para ejecutar los escenarios
    .pause(5)
    .exec(getSpecificGame())
    .repeat(2) { //En el metodo scenario tambien podremos usar el metodo repeat y dentro podemos llamar las funciones creadas
      getAllVideoGames()
    }

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
