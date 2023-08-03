package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CheckResponseBodyAndExtract extends Simulation{

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

  val scn = scenario("Check with JSON Path")

    .exec(http("Get specific game")
    .get("/videogame/1")
    .check(jsonPath("$.name").is("Resident Evil 4")))//con jsonPath podremos checkear un valor del body rs y compararlo contra otro
    //dentro del metodo debemos pasar el path del json con o con los campos que queremos evaluar

    .exec(http("Get all video games")
    .get("/videogame")
    .check(jsonPath("$[1].id").saveAs("gameId"))) //de esta forma podremos extraer del body rs algun dato en particular y guardarlo en una variable para usos posteriores.
    .exec { session => println(session); session } //Forma de imprimir informacion base de la sesion ejecutada (Headers, Variables, etc)

    .exec(http("Get specific game")
    .get("/videogame/#{gameId}") //Para utilizar una llave con un valor extraido lo hacemos de esta forma #{key}
    .check(jsonPath("$.name").is("Gran Turismo 3"))
    .check(bodyString.saveAs("responseBody"))) //bodyString.saveAs permite guardar en una variable el body del rs
    .exec { session => println(session("responseBody").as[String]); session } //con session(variable) podremos imprimir el body en consola. Importante hacer un cast con .as[String]

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
