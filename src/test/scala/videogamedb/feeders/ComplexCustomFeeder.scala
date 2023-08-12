package videogamedb.feeders

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Random

class  ComplexCustomFeeder extends Simulation {

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  var idNumbers = (1 to 10).iterator
  val rnd = new Random() //Variable que utiliza la clase Random
  val now = LocalDate.now() //Variable que devuelve la hora actual
  val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd") //Formato de fecha personalizado

  def randomString(length: Int) = { //Metodo que devuelve un string aleatorio con solo letras
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def getRandomDate(startDate: LocalDate, random: Random): String = { //Metodo que devuelve una fecha aleatoria con el formato indicado
    startDate.minusDays(random.nextInt(30)).format(pattern)
  }

  val customFeeder = Iterator.continually(Map( //Map que almacena los datos que pasaremos a nuestro escenario construidos con los metodos de datos aleatorios
    "gameId" -> idNumbers.next(),
    "name" -> ("Game-" + randomString(5)), //Asigna un String de 5 caracteres
    "releaseDate" -> getRandomDate(now, rnd),
    "reviewScore" -> rnd.nextInt(100), //Asigna un numero aleatoriamente entre 0 al 10
    "category" -> ("Category-" + randomString(6)),
    "rating" -> ("Rating-" + randomString(4))
  ))

  def authenticate() = { //Metodo autenticacion
    exec(http("Authenticate")
    .post("/authenticate")
    .body(StringBody("{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}"))
    .check(jsonPath("$.token").saveAs("jwtToken")))
  }

  def createNewGame() = { //Creacion del escenario donde pasamos el header, body, path, auntenticador
    repeat(10) { //Indicamos que se repetira 10 veces el consumo del metodo
      feed(customFeeder) //El body del Rq se alimentara de la data del Map Creado
        .exec(http("Create new game - #{name}") //Nombre del escenario
        .post("/videogame") //Path y Metodo
        .header("authorization", "Bearer #{jwtToken}") //Pasamoe en el Header el autorization
        .body(ElFileBody("bodies/newGameTemplate.json")).asJson //Body. con ElFileBody indicamos que se recibira un template, la ruta y .asJson que sera de formato Json
        .check(bodyString.saveAs("responseBody"))) //Guardamos el body con la key responseBody
        .exec { session => println(session("responseBody").as[String]); session} //Imprimimos el body del RS
        .pause(1) //Pausa de un segundo
    }
  }

  val scn = scenario("Complex Custom Feeder") //Llamado de los metodos que contienen la configuracion de ejecucion de los servicios
    .exec(authenticate())
    .exec(createNewGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
