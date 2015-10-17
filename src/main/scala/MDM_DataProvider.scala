import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.server.Directives._
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

case class Country(country: String, isAgent: Boolean)

trait CountryJsonProtocol extends DefaultJsonProtocol {
  implicit val formatCountry = jsonFormat2(Country.apply)
}

object MDM_DataProvider extends CountryJsonProtocol {
  val source = scala.io.Source.fromURL(getClass.getResource("static/SchenkerCountries.json"))
  val countries: List[Country] = source.mkString.parseJson.convertTo[List[Country]]
}
