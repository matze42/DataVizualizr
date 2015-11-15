import akka.http.scaladsl.server.Directives._
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

case class Adress(streetname: String, plz: BigDecimal, city: String)

case class Country(country: String, isAgent: Boolean)


trait AddressJsonProtocol extends DefaultJsonProtocol {
  implicit val formatAddress = jsonFormat3(Adress.apply)
  implicit val formatSC = jsonFormat2(SchenkerCountry.apply)
}

object Main extends App with AddressJsonProtocol {

  val jsonString =
  """
    |[
    |{"name": "HK", "zahl": 0},
    |{"name": "YE", "zahl": 1}
    |]
  """.stripMargin
//  val jsonString =
//    """
//      |[
//      |["HK", 0],
//      |["PS", 1],
//      |["PT", 0],
//      |["HN", 1],
//      |["PY", 1],
//      |["HR", 0],
//      |["YE", 1]
//      |]
//    """.stripMargin


  val address = new Adress("street", 123, "city")
  println("poso's default toString: %s".format(address))
  val addressJVal = address.toJson
  println("JValue's toString: %s".format(addressJVal))
  val addressJson = addressJVal.prettyPrint
  println("pretty-printing: %s".format(addressJson))


  val listAddresses = List(address, address)



//  println(jsonString.parseJson.convertTo[List[Country]] )



  println("prPPR: %s".format(ttt))

}

