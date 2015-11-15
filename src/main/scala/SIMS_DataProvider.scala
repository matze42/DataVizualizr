import spray.json.DefaultJsonProtocol
import spray.json._


/*
Data Format
Country code;Company Name;COUS ID;No. of profiles
AE;SCHENKER MIDDLE EAST FZE;AEDXBLE0004;20
AE;SCHENKER (L.L.C);AEDXBBR0004;163
AE;Schenker Logistics L.L.C.;AEAUHLE0002;71

 println("Month, Income, Expenses, Profit")
  val bufferedSource = io.Source.fromFile("/tmp/finance.csv")
  for (line <- bufferedSource.getLines) {
    val cols = line.split(",").map(_.trim)
    // do whatever you want with the columns here
    println(s"${cols(0)}|${cols(1)}|${cols(2)}|${cols(3)}")
  }
  bufferedSource.close

 */


case class SIMSProfile(countryCode: String, companyName: String, COUS_Code: String, numberProfiles: Int)


object SIMS_DataProvider {

  var legalEntityProfiles = scala.collection.mutable.Map.empty[String, Int]
  var legalEntityInfo = scala.collection.mutable.Map.empty[String, SIMSProfile]
  var countrySIMS = Set.empty[String]

  def readSIMS_CSV(): Unit = {
    val bufferedSource = io.Source.fromURL(getClass.getResource("data/active_employee_profile_per_company.csv"))

    //      Country code;Company Name;COUS ID;No. of profiles
    //      AE;SCHENKER MIDDLE EAST FZE;AEDXBLE0004;20
    var lineNo = 1
    for (line <- bufferedSource.getLines.drop(1)) {
      lineNo += 1
      val Array(countryCode, companyName, cousCode, profiles) = line.split(";").map(_.trim)
      if (legalEntityInfo.keySet.contains(cousCode)) {
        println(s"GRANDE PROBLEMO: Duplicate COUS CODE:[$cousCode] in line $lineNo")
      }
      legalEntityProfiles += (cousCode -> profiles.toInt)
      legalEntityInfo += (cousCode -> SIMSProfile(countryCode, companyName, cousCode, profiles.toInt))
      countrySIMS += countryCode

    }
    bufferedSource.close
  }


  def simsUsedData() = {
    readSIMS_CSV()
    val cols = List(Col(Some("Country"), None, "string", Some("domain")), Col(Some("SIMS Used"), None, "number", Some("data")),
      Col(Some("Tooltip"), None, "string", Some("tooltip")))

    val otherCountries: List[ServiceCountryLive] = MDM_DataProvider.countries.filter(c => !countrySIMS.contains(c.country)).map(c => ServiceCountryLive(c.country, live = false))

    val allCountries = countrySIMS.map(c => ServiceCountryLive(c, live = true)).toList ::: otherCountries
    val rows = allCountries.map(c => Row(c.country, if (c.live) 2 else 1, MasterDataProvider.getCountryName(c.country)))
    GoogleChartsDataProvider.gc_data(cols, rows)
  }

}

