import spray.json.DefaultJsonProtocol
import spray.json._

import scala.collection.mutable.ListBuffer

case class Country(country: String, isAgent: Boolean)

case class MDMCountry(country: String, live: Boolean)

trait CountryJsonProtocol extends DefaultJsonProtocol {
  implicit val formatCountry = jsonFormat2(Country.apply)
  implicit val formatMDMCountry = jsonFormat2(MDMCountry.apply)
}

object MDM_DataProvider extends CountryJsonProtocol {
  val countriesSource = scala.io.Source.fromURL(getClass.getResource("static/SchenkerCountries.json"))
  val countries: List[Country] = countriesSource.mkString.parseJson.convertTo[List[Country]]

  val mdmCountriesSource = scala.io.Source.fromURL(getClass.getResource("static/MDMCountries.json"))
  val mdmCountries: List[MDMCountry] = mdmCountriesSource.mkString.parseJson.convertTo[List[MDMCountry]]

  case class Col(label: Option[String], pattern: Option[String], gc_type: String)

  case class Row(country: String, value: Int)

  def mdmData() = {
    val cols = List(Col(Some("Country"), None, "string"), Col(Some("MDM Enabled"), None, "number"))
    val rows = new ListBuffer[Row]
    for (c <- wurst()) {
      rows += Row(c.country, if (c.live) 2 else 1)
    }
    GoogleChartsDataProvider.gc_data(cols, rows.toList)
  }

  def wurst() = {

    var temp = new ListBuffer[MDMCountry]
    for (c <- countries
         if !c.isAgent) {
      temp += MDMCountry(c.country, live=false)
    }
    temp.toList ::: mdmCountries
  }


  def schenkerCountries() = {
    val cols = List(Col(Some("Country"), None, "string"), Col(Some("Agent = 1 / Schenker = 2"), None, "number"))
    val rows = new ListBuffer[Row]
    for (c <- countries) {
      rows += Row(c.country, if (c.isAgent) 1 else 2)
    }
    GoogleChartsDataProvider.gc_data(cols, rows.toList)
  }
}

