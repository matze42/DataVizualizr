import org.scalatest.FunSuite

/**
  * Created by matthiasheck on 31.10.15.
  */
class SIMS_DataProvider$Test extends FunSuite {
  test(" read SIMS CSV") {
    SIMS_DataProvider.readSIMS_CSV()

    assert(SIMS_DataProvider.legalEntityProfiles("THBKKBR0012") == 16)
    assert(SIMS_DataProvider.legalEntityInfo("ATVIERC0001").companyName == "SCHENKER & CO. AG")
  }
}
