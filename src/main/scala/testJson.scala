import spray.json._

case class Adress(streetname: String, plz: BigDecimal, city: String)

case class SchenkerCountry(name: String, zahl: Integer)


trait AddressJsonProtocol extends DefaultJsonProtocol {

  implicit val formatAddress = jsonFormat3(Adress.apply)
  implicit val formatSchenkerCountry = jsonFormat2(SchenkerCountry.apply)
}

object Main extends App with AddressJsonProtocol {

  val jsonString =
    """
      |[
      |["HK", 0],
      |["PS", 1],
      |["PT", 0],
      |["HN", 1],
      |["PY", 1],
      |["HR", 0],
      |["YE", 1]
      |]
    """.stripMargin


  val address = new Adress("street", 123, "city")
  println("poso's default toString: %s".format(address))
  val addressJVal = address.toJson
  println("JValue's toString: %s".format(addressJVal))
  val addressJson = addressJVal.prettyPrint
  println("pretty-printing: %s".format(addressJson))


  val listAddresses = List(address, address)

  println("pretty-printing: %s".format(listAddresses.toJson.prettyPrint))

  var ttt: List[SchenkerCountry] = jsonString.parseJson.convertTo[List[SchenkerCountry]]



  println("prPPR: %s".format(ttt))
}

