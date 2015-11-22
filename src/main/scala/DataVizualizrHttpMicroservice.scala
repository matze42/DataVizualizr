import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContextExecutor

trait Service extends TwirlSupport {
  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  def config: Config

  val logger: LoggingAdapter

  val routes = {
    // Logging ausgeschaltet durch auskommentieren
    //  logRequestResult("akka-http-microservice") {
    pathSingleSlash {
      getFromResource("static/index.html")
    } ~
      pathPrefix("static") {
        path("AdminLTE" / "MDMCountries") {
          complete(html.main.render("/api/mdm-countries", "regions_div", "png", "MDM CPI Countries Live"))
        } ~
          path("AdminLTE" / "SchenkerCountries") {
            complete(html.main.render("/api/schenker-countries-agents", "regions_div", "png", "Schenker & Schenker Agent Countries"))
          } ~
          path("AdminLTE" / "SIMSCountries") {
            complete(html.main.render("/api/sims-countries", "regions_div", "png", "Users with SIMS Profile in country"))
          } ~
          path("AdminLTE" / "ODMCountries") {
            complete(html.main.render("/api/odm-countries", "regions_div", "png", "Countries with documents in ODM Archive"))
          } ~
          // optionally compresses the response with Gzip or Deflate
          // if the client accepts compressed responses
          encodeResponse {
            // serve up static content from a JAR ressource
            getFromResourceDirectory("static")
          }
      } ~
      pathPrefix("api") {
        path("schenker-countries-agents") {
          complete {
            //html.twirltest.render()
            Service_DataProvider.schenkerCountries()
          }
        } ~
          path("mdm-countries") {
            complete {
              Service_DataProvider.mdmData()
            }
          } ~
          path("sims-countries") {
            complete {
              Service_DataProvider.simsData()
            }
          } ~
          path("odm-countries") {
            complete {
              Service_DataProvider.odmData()
            }
          }
      }
  }
  //  }
}

object DataVizualizrHttpMicroservice extends App with Service {
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}