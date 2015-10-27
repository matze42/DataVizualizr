import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContextExecutor

import play.twirl.api.{Xml, Txt, Html}
import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.ContentType

trait TwirlSupport {
  //https://github.com/btomala/akka-http-twirl

  /** Serialize Twirl `Html` to `text/html`. */
  implicit val twirlHtmlMarshaller = twirlMarshaller[Html](`text/html`)

  /** Serialize Twirl `Txt` to `text/plain`. */
  implicit val twirlTxtMarshaller = twirlMarshaller[Txt](`text/plain`)

  /** Serialize Twirl `Xml` to `text/xml`. */
  implicit val twirlXmlMarshaller = twirlMarshaller[Xml](`text/xml`)

  /** Serialize Twirl formats to `String`. */
  protected def twirlMarshaller[A <: AnyRef : Manifest](contentType: ContentType): ToEntityMarshaller[A] =
    Marshaller.StringMarshaller.wrap(contentType)(_.toString)

}

trait Service extends TwirlSupport {
  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  def config: Config

  val logger: LoggingAdapter

  val fileNAme = "static/data.json"


  val routes = {
    logRequestResult("akka-http-microservice") {
      pathSingleSlash {
        getFromResource("static/index-old.html")
      } ~
        pathPrefix("staticAdminLTE") {
          path("main") {
            complete(html.main.render("/api/mdm-countries", "regions_div", "png"))
          }~
          path("MDMCountries"){
            complete(html.main.render("/api/mdm-countries", "regions_div", "png"))
          }~
          path("SchenkerCountries"){
            complete(html.main.render("/api/schenker-countries-agents", "regions_div", "png"))
          }
        } ~
        pathPrefix("static") {
          // optionally compresses the response with Gzip or Deflate
          // if the client accepts compressed responses
          encodeResponse {
            // serve up static content from a JAR ressource
            getFromResourceDirectory("static")
          }
        } ~
        pathPrefix("api") {
          path("test-data") {
            getFromResource(fileNAme)
          } ~
            path("schenker-countries-agents") {
              complete {
                //html.twirltest.render()
                MDM_DataProvider.schenkerCountries()
              }
            } ~
            path("mdm-countries") {
              complete {
                MDM_DataProvider.mdmData()
              }
            }
        }
    }
  }
}

object DataVizualizrHttpMicroservice extends App with Service {
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
