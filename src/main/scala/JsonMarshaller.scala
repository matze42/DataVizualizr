
import spray.json.DefaultJsonProtocol

case class SchenkerCountry(countryCode: String, isAgent: Boolean)


trait MyProtocol extends DefaultJsonProtocol {
  implicit val schenkerCountry = jsonFormat2(SchenkerCountry.apply)
}

class JsonMarshaller extends MyProtocol {

  import spray.json._

  val source = """{ "some": "JSON source" }"""


  val json = SchenkerCountry("DE", true).toJson

  def show = println(jsonList)

  val jsonList = List(SchenkerCountry("RU", true), SchenkerCountry("FR", false)).toJson





}

object JsonMarshaller {
  val googleChartsLiteral =
    """
      |{
      |  "cols": [
      |    {"id":"","label":"Country","pattern":"","type":"string"},
      |    {"id":"","label":"MDM Enabled","pattern":"","type":"number"}
      |  ],
      |  "rows": [
      |    {"c":[{"v":"Poland","f":null},{"v":1,"f":null}]},
      |    {"c":[{"v":"Portugal","f":null},{"v":1,"f":null}]},
      |    {"c":[{"v":"India","f":null},{"v":1,"f":null}]},
      |    {"c":[{"v":"Argentina","f":null},{"v":1,"f":null}]},
      |    {"c":[{"v":"Brazil","f":null},{"v":1,"f":null}]},
      |    {"c":[{"v":"Iceland","f":null},{"v":1,"f":null}]}
      |  ]
      |}
    """.stripMargin






}