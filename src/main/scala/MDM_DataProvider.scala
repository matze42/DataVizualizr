import spray.json.DefaultJsonProtocol
import spray.json._

case class SchenkerCountry(country: String, isAgent: Boolean)

case class ServiceCountryLive(country: String, live: Boolean)

trait CountryJsonProtocol extends DefaultJsonProtocol {
  implicit val formatCountry = jsonFormat2(SchenkerCountry.apply)
  implicit val formatServiceCountryLive = jsonFormat2(ServiceCountryLive.apply)
}

object MDM_DataProvider extends CountryJsonProtocol {
  val countriesSource = scala.io.Source.fromURL(getClass.getResource("data/SchenkerCountries.json"))
  val countries: List[SchenkerCountry] = countriesSource.mkString.parseJson.convertTo[List[SchenkerCountry]]

  val mdmCountriesSource = scala.io.Source.fromURL(getClass.getResource("data/MDMCountries.json"))
  val mdmCountries: List[ServiceCountryLive] = mdmCountriesSource.mkString.parseJson.convertTo[List[ServiceCountryLive]]

  val odmCountriesSource = scala.io.Source.fromURL(getClass.getResource("data/ODMCountries.json"))
  val odmCountries: List[ServiceCountryLive] = odmCountriesSource.mkString.parseJson.convertTo[List[ServiceCountryLive]]

  def mdmData() = {
    val cols = List(Col(Some("Country"), None, "string", Some("Domain")), Col(Some("MDM Enabled"), None, "number", Some("Data")),
      Col(Some("Tooltip"), None, "string", Some("tooltip")))
    val rows = mdmSchenkerCountriesMerged().map(c => Row(c.country, if (c.live) 2 else 1, MasterDataProvider.getCountryName(c.country)))
    GoogleChartsDataProvider.gc_data(cols, rows)
  }

  private def mdmSchenkerCountriesMerged() = {
    countries.filter(c => !mdmCountries.exists(m => m.country == c.country)).map(c => ServiceCountryLive(c.country, live = false)) ::: mdmCountries
  }

  def odmData() = {
    val cols = List(Col(Some("Country"), None, "string", Some("Domain")), Col(Some("ODM used"), None, "number", Some("Data")),
      Col(Some("Tooltip"), None, "string", Some("tooltip")))
    val rows = odmSchenkerCountriesMerged().map(c => Row(c.country, if (c.live) 2 else 1, MasterDataProvider.getCountryName(c.country)))
    GoogleChartsDataProvider.gc_data(cols, rows)
  }

  private def odmSchenkerCountriesMerged() = {
    countries.filter(c => !odmCountries.exists(m => m.country == c.country)).map(c => ServiceCountryLive(c.country, live = false)) ::: odmCountries
  }

  def schenkerCountries() = {
    val cols = List(Col(Some("Country"), None, "string", Some("Domain")), Col(Some("Agent = 1 / Schenker = 2"), None, "number", Some("Data")),
      Col(Some("Tooltip"), None, "string", Some("tooltip")))
    val rows = countries.map(c => Row(c.country, if (c.isAgent) 1 else 2, MasterDataProvider.getCountryName(c.country)))
    GoogleChartsDataProvider.gc_data(cols, rows)
  }

}

