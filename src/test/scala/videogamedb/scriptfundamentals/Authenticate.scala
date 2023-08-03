package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class Authenticate extends Simulation {

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    //Pasamos los headers necesarios para consumir el servicio
    .acceptHeader("application/json") //Header accept
    .contentTypeHeader("application/json") //Header contentType

  def authenticate() = {
    exec(http("Authenticate")
    .post("/authenticate") //Consumimos un metodo post indicando el path
    .body(StringBody("{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}")) //Pasamos el body rq dentro del StringBody
    .check(jsonPath("$.token").saveAs("jwtToken"))) //Del body rs guardamos en una variable lo devuelto en el campo .token
  }

  def createNewGame() = { //Servicio que necesita una autenticacion para su correcto funcionamiento
    exec(http("Create new game")
    .post("/videogame")
      .header("Authorization", "Bearer #{jwtToken}") //Pasamos la autenticacion obtenida anteriormente mediante un header
    .body(StringBody( //Pasamos el body del post
      "{\n  \"category\": \"Platform\",\n  \"name\": \"Mario\",\n  \"rating\": \"Mature\",\n  \"releaseDate\": \"2012-05-04\",\n  \"reviewScore\": 85\n}"
    )))
  }

  val scn = scenario("Authenticate") //creamos el escenario con los dos metodos a consumir
    .exec(authenticate())
    .exec(createNewGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
