import spray.json.DefaultJsonProtocol
import spray.json._

case class SchenkerCountry(country: String, isAgent: Boolean)

case class ServiceCountryLive(country: String, live: Boolean)

trait CountryJsonProtocol extends DefaultJsonProtocol {
  implicit val formatCountry = jsonFormat2(SchenkerCountry.apply)
  implicit val formatServiceCountryLive = jsonFormat2(ServiceCountryLive.apply)
}

object Service_DataProvider extends CountryJsonProtocol {
  val countriesSource = scala.io.Source.fromURL(getClass.getResource("data/SchenkerCountries.json"))
  val countries: List[SchenkerCountry] = countriesSource.mkString.parseJson.convertTo[List[SchenkerCountry]]

  val mdmCountriesSource = scala.io.Source.fromURL(getClass.getResource("data/MDM_CPI_Countries.json"))
  val mdmCountries: List[ServiceCountryLive] = mdmCountriesSource.mkString.parseJson.convertTo[List[ServiceCountryLive]]

  val odmCountriesSource = scala.io.Source.fromURL(getClass.getResource("data/ODMCountries.json"))
  val odmCountries: List[ServiceCountryLive] = odmCountriesSource.mkString.parseJson.convertTo[List[ServiceCountryLive]]

  def mdmData() = {
    serviceData(mergeServiceAndSchenkerCountries(mdmCountries), "MDM CPI Live")
  }

  def odmData() = {
    serviceData(mergeServiceAndSchenkerCountries(odmCountries), "Document in Archive for Country")
  }

  def simsData() = {
    SIMS_DataProvider.readSIMS_CSV()

    val otherCountries: List[ServiceCountryLive] = countries.filter(c => !SIMS_DataProvider.countrySIMS.contains(c.country)).map(c => ServiceCountryLive(c.country, live = false))

    val simsCountries = SIMS_DataProvider.countrySIMS.map(c => ServiceCountryLive(c, live = true)).toList ::: otherCountries


    serviceData(mergeServiceAndSchenkerCountries(simsCountries), "User with SIMS Profile")
  }

  private def mergeServiceAndSchenkerCountries(serviceCountries: List[ServiceCountryLive]) = {
    countries.filter(c => !serviceCountries.exists(m => m.country == c.country)).map(c => ServiceCountryLive(c.country, live = false)) ::: serviceCountries
  }

  private def serviceData(countries: List[ServiceCountryLive], description: String) = {
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

