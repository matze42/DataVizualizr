import spray.json.DefaultJsonProtocol
import spray.json._

case class SchenkerCountry(country: String, isAgent: Boolean)

case class ServiceCountry(country: String, live: Boolean)

case class ServiceCountriesLive(data: List[ServiceCountry])


trait CountryJsonProtocol extends DefaultJsonProtocol {
  implicit val formatCountry = jsonFormat2(SchenkerCountry.apply)
  implicit val formatServiceCountry = jsonFormat2(ServiceCountry.apply)
  implicit val formatServiceCountryLive = jsonFormat1(ServiceCountriesLive.apply)

}

object Service_DataProvider extends CountryJsonProtocol {
  val countriesSource = scala.io.Source.fromURL(getClass.getResource("data/SchenkerCountries.json"))
  val countries: List[SchenkerCountry] = countriesSource.mkString.parseJson.convertTo[List[SchenkerCountry]]

  val mdmCountriesSource = scala.io.Source.fromURL(getClass.getResource("data/MDM_CPI_Countries.json"))
  val mdmCountries: ServiceCountriesLive = mdmCountriesSource.mkString.parseJson.convertTo[ServiceCountriesLive]

  val odmCountriesSource = scala.io.Source.fromURL(getClass.getResource("data/ODMCountries.json"))
  val odmCountries: ServiceCountriesLive = odmCountriesSource.mkString.parseJson.convertTo[ServiceCountriesLive]

  val simsCountriesSource = scala.io.Source.fromURL(getClass.getResource("data/SIMS_Countries.json"))
  val simsCountries: ServiceCountriesLive = simsCountriesSource.mkString.parseJson.convertTo[ServiceCountriesLive]

  def mdmData() = {
    serviceData(mergeServiceAndSchenkerCountries(mdmCountries.data), "MDM CPI Live")
  }

  def odmData() = {
    serviceData(mergeServiceAndSchenkerCountries(odmCountries.data), "Document in Archive for Country")
  }

  def simsData() = {
    serviceData(mergeServiceAndSchenkerCountries(simsCountries.data), "User with SIMS Profile")
  }

  private def mergeServiceAndSchenkerCountries(serviceCountries: List[ServiceCountry]) = {
    countries.filter(c => !serviceCountries.exists(m => m.country == c.country)).map(c => ServiceCountry(c.country, live = false)) ::: serviceCountries
  }

  private def serviceData(countries: List[ServiceCountry], description: String) = {
    val cols = List(Col(Some("Country"), None, "string", Some("Domain")), Col(Some(description), None, "number", Some("Data")),
      Col(Some("Tooltip"), None, "string", Some("tooltip")))
    val rows = countries.map(c => Row(c.country, if (c.live) 2 else 1, MasterDataProvider.getCountryName(c.country)))
    GoogleChartsDataProvider.gc_data(cols, rows)
  }


  def schenkerCountries() = {
    val cols = List(Col(Some("Country"), None, "string", Some("Domain")), Col(Some("Agent = 1 / Schenker = 2"), None, "number", Some("Data")),
      Col(Some("Tooltip"), None, "string", Some("tooltip")))
    val rows = countries.map(c => Row(c.country, if (c.isAgent) 1 else 2, MasterDataProvider.getCountryName(c.country)))
    GoogleChartsDataProvider.gc_data(cols, rows)
  }


}

