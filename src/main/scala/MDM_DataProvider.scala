import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.server.Directives._
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.collection.mutable.ListBuffer

case class Country(country: String, isAgent: Boolean)

trait CountryJsonProtocol extends DefaultJsonProtocol {
  implicit val formatCountry = jsonFormat2(Country.apply)
}

object MDM_DataProvider extends CountryJsonProtocol {
  val source = scala.io.Source.fromURL(getClass.getResource("static/SchenkerCountries.json"))
  val countries: List[Country] = source.mkString.parseJson.convertTo[List[Country]]


  case class Col(label: Option[String], pattern: Option[String], gc_type: String)

  case class Row(country: String, value: Int)

  def mdmData3() = {

    val cols = List(Col(Some("Country"), None, "string"), Col(Some("MDM Enabled"), None, "number"))


    val rows = new ListBuffer[Row]
    for (c <- countries) {
      rows += Row(c.country, if (c.isAgent) 1 else 2)
    }
    GoogleChartsDataProvider.gc_data(cols, rows.toList)

  }
}

