import akka.actor.ActorSystem
import akka.event.{LoggingAdapter, Logging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.IOException
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.math._
import spray.json.DefaultJsonProtocol
import scala.io.Source


//trait Protocols2 extends DefaultJsonProtocol {
//  implicit val ipInfoFormat = jsonFormat5(IpInfo.apply)
//  implicit val ipPairSummaryRequestFormat = jsonFormat2(IpPairSummaryRequest.apply)
//  implicit val ipPairSummaryFormat = jsonFormat3(IpPairSummary.apply)
//}

trait Service {
  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  def config: Config

  val logger: LoggingAdapter

  val fileNAme = "static/data.json"


  val routes = {
    logRequestResult("akka-http-microservice") {
      pathSingleSlash {
        getFromResource("static/index.html")
      } ~
        pathPrefix("test") {
          (get & path(Segment)) { name =>
        complete {"DV: hello World"+name}
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
            getFromResource("static/data.json")
          } ~
            path("schenker-countries-agents") {
              complete {
                JsonMarshaller.googleChartsLiteral
              }
            }
          //          (get) {
          //          complete {
          //                  scala.io.Source.fromFile("/Users/matthiasheck/IdeaProjects/DataVizualizr/target/scala-2.11/classes/static/data.json").getLines.mkString
          //                  }
          //          }
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
