package videogamedb.finalsimulation

import io.gatling.http.Predef._
import io.gatling.core.Predef._

class JeffFinalSimulationII extends Simulation{
  val httpProtocol = http.baseUrl("https://petstore.swagger.io/v2")
    .acceptHeader("application/json")

  def getPetSold() = {
    exec(
      http("get pet by status")
        .get("/pet/findByStatus?status=sold")
        .check(bodyString.saveAs("findByStatusResponseBody"))
        .check(jsonPath("$[0].id").saveAs("petId"))
        .check(jsonPath("$[0].name").is("doggie"))
        .check(status.in(200 to 204))
        .check(status.not(404))
    )

  }

  def getPetById() = {
    exec(
      http("get pet by id #{petId}")
        .get("/pet/#{petId}")
        .check(bodyString.saveAs("getPetResponseBody"))
    )//.exec{session => println(session("getPetResponseBody").as[String]);session}
  }

  val scn = scenario("load simulation")
    .exec(getPetSold().pause(2 ,10))
    .repeat(2){
      getPetById()
    }


  setUp(
    scn.inject(
      atOnceUsers(2)
      //rampUsers(5).during(5)
    ).protocols(httpProtocol)
  )
}
