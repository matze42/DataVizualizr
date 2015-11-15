import org.scalatest.FunSuite

/**
  * Created by matthiasheck on 01.11.15.
  */
class MasterDataProvider$Test extends FunSuite {


  test("test json loading") {
    println("Starting json load test of master countries")
    assert(MasterDataProvider.getCountryName("DZ").toUpperCase == "Algeria".toUpperCase)
    assert(MasterDataProvider.getCountryName("DE").toUpperCase == "GErmany".toUpperCase)
  }

}
®