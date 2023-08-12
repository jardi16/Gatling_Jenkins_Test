package videogamedb.commandline

import io.gatling.core.Predef._
import io.gatling.http.Predef._

//Para ejecutar con MVN desde consola se corre el siguiente comando
// mvn gatling:test -D"gatling.simulationClass=videogamedb.commandline.RuntimeParameters"

//Para ejecutar con los parametros perosnalizados se corre desde consola con el siguiente comando:
//mvn gatling:test -D"gatling.simulationClass=videogamedb.commandline.RuntimeParameters" -DUSERS=20 -DRAMP_DURATION=20 -DTEST_DURATION=20
class RuntimeParameters extends Simulation {

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

  //Forma de parametrizar valores que se pasan en la ejecucion
  def USERCOUNT = System.getProperty("USERS", "5").toInt //Si no se pasa ningun valor desde consola al ejecutar con maven, se utilizara por defecto el 5
  def RAMPDURATION = System.getProperty("RAMP_DURATION", "10").toInt
  def TESTDURATION = System.getProperty("TEST_DURATION", "30").toInt

  //Imprimimos los valores antes de la ejecucion
  before {
    println(s"Running test with ${USERCOUNT} users")
    println(s"Ramping users over ${RAMPDURATION} seconds")
    println(s"Total test duration: ${TESTDURATION} seconds")
  }

  def getAllVideoGames() = {
    exec(
      http("Get all video games")
        .get("/videogame")
    ).pause(1)
  }

  val scn = scenario("Run from command line")
    .forever {
      exec(getAllVideoGames())
    }

  setUp(
    scn.inject(
      nothingFor(5),
      rampUsers(USERCOUNT).during(RAMPDURATION) //Pasamos las llaves de los valores
    )
  ).protocols(httpProtocol)
    .maxDuration(TESTDURATION)

}
