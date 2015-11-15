import spray.json.DefaultJsonProtocol
import spray.json._


/*
Source: http://data.okfn.org/data/core/country-codes

Field Name	Order	Type (Format)	Description
name	1	string	Country's official English short name
name_fr	2	string	Country's offical French short name
ISO3166-1-Alpha-2	3	string	Alpha-2 codes from ISO 3166-1
ISO3166-1-Alpha-3	4	string	Alpha-3 codes from ISO 3166-1 (synonymous with World Bank Codes)
ISO3166-1-numeric	5	integer	Numeric codes from ISO 3166-1 (synonymous with UN Statistics M49 Codes)
ITU	6	string	Codes assigned by the International Telecommunications Union
MARC	7	string	MAchine-Readable Cataloging codes from the Library of Congress
WMO	8	string	Country abbreviations by the World Meteorological Organization
DS	9	string	Distinguishing signs of vehicles in international traffic
Dial	10	string	Country code from ITU-T recommendation E.164, sometimes followed by area code
FIFA	11	string	Codes assigned by the Fédération Internationale de Football Association
FIPS	12	string	Codes from the U.S. standard FIPS PUB 10-4
GAUL	13	integer	Global Administrative Unit Layers from the Food and Agriculture Organization
IOC	14	string	Codes assigned by the International Olympics Committee
currency_alphabetic_code	15	string	ISO 4217 currency alphabetic code
currency_country_name	16	string	ISO 4217 country name
currency_minor_unit	17	integer	ISO 4217 currency number of minor units
currency_name	18	string	ISO 4217 currency name
currency_numeric_code	19	integer	ISO 4217 currency numeric code
is_independent	20	string	Country status, based on the CIA World Factbook

 */
case class MasterCountry(name: String, name_fr: String, ISO3166_1_Alpha_2: String, ISO3166_1_Alpha_3: String,
                         ISO3166_1_numeric: String, ITU: String, MARC: String, WMO: String, DS: String, Dial: String,
                         FIFA: String, FIPS: String, GAUL: String, IOC: String, currency_alphabetic_code: String,
                         currency_country_name: String, currency_minor_unit: String, currency_name: String,
                         currency_numeric_code: String, is_independent: String)

trait MasterCountryJsonProtocol extends DefaultJsonProtocol {
  implicit val formatCountry = jsonFormat20(MasterCountry.apply)
}

object MasterDataProvider extends MasterCountryJsonProtocol {
  val origMasterCountriesSource = scala.io.Source.fromURL(getClass.getResource("data/country-codes.json"))
  val masterCountriesSource = origMasterCountriesSource.mkString.replaceAll("ISO3166-1-Alpha-2", "ISO3166_1_Alpha_2").replaceAll("ISO3166-1-Alpha-3", "ISO3166_1_Alpha_3").replaceAll("ISO3166-1-numeric", "ISO3166_1_numeric")
  val countries: List[MasterCountry] = masterCountriesSource.parseJson.convertTo[List[MasterCountry]]

  private var countryMap = Map.empty[String, String]

  countries.foreach(c => countryMap += (c.ISO3166_1_Alpha_2 -> c.name))

  def getCountryName(countryCode: String) = {
    if (countryMap.contains(countryCode))
      countryMap(countryCode)
    else {
      println(s"WARNING: Country code [$countryCode] unknown!")
      s"UNKNOWN CC: $countryCode" }
  }
}

