import akka.http.scaladsl.server.Directives._
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import scala.collection.mutable.ListBuffer

case class GC_Col(id: Option[String], label: Option[String], pattern: Option[String], `type`: String)
case class GC_Row(c: List[GC_Cell])
case class GC_Cell(v: String, f: Option[String], p: Option[String])

// TODO add property "p"
case class GC_Data(cols: List[GC_Col], rows: List[GC_Row])

trait GC_DataJsonProtocol extends DefaultJsonProtocol {
  implicit val formatGC_Cell = jsonFormat3(GC_Cell.apply)
  implicit val formatGC_Row = jsonFormat1(GC_Row.apply)
  implicit val formatGC_Col = jsonFormat4(GC_Col.apply)
  implicit val formatGC_Data = jsonFormat2(GC_Data.apply)
}

object GoogleChartsDataProvider extends GC_DataJsonProtocol {

  case class Col(label: Option[String], pattern: Option[String], gc_type: String)
  case class Row(country: String, value: Int)

  def mdmData3() = {
    // Reference for Google Chart Data Structure
    // https://developers.google.com/chart/interactive/docs/reference#DataTable

    val cols = List(Col(Some("Country"), None, "string"), Col(Some("MDM Enabled"), None, "number"))

    var spalten = new ListBuffer[GC_Col]

    var i = 0
    for (a <- cols) {
      i += 1
      spalten += GC_Col(Some("col_" + i), a.label, a.pattern, a.gc_type)
    }

    val rows = new ListBuffer[Row]
    for (c <- MDM_DataProvider.countries) {
      rows += Row(c.country, if (c.isAgent) 1 else 2)
    }
    //    val rows = List(Row("France", 1), Row("Spain", 2), Row("China", 3))

    val zeilen = new ListBuffer[GC_Row]
    for (a <- rows.toList) {
      zeilen += GC_Row(List(GC_Cell(a.country, None, None), GC_Cell(a.value.toString, None, None)))
    }

    GC_Data(spalten.toList, zeilen.toList).toJson
  }



  def mdmData2() = {
    // Reference for Google Chart Data Structure
    // https://developers.google.com/chart/interactive/docs/reference#DataTable

    val cols = List(Col(Some("Country"), None, "string"), Col(Some("MDM Enabled"), None, "number"))

    var spalten = new ListBuffer[GC_Col]

    var i = 0
    for (a <- cols) {
      i += 1
      spalten += GC_Col(Some("col_" + i), a.label, a.pattern, a.gc_type)
    }

    val rows = List(Row("France", 1), Row("Spain", 2), Row("China", 3))

    val zeilen = new ListBuffer[GC_Row]
    for (a <- rows) {
      zeilen += GC_Row(List(GC_Cell(a.country, None, None), GC_Cell(a.value.toString, None, None)))
    }

    GC_Data(spalten.toList, zeilen.toList).toJson
  }

  def mdmData() = {
    """
      |{
      |  "cols": [
      |  {"id":"","label":"Country","pattern":"","type":"string"},
      |  {"id":"","label":"MDMD Enabled","pattern":"","type":"number"}
      |  ],
      |  "rows": [
      |  {"c":[{"v":"France","f":null},{"v":1,"f":null}]},
      |  {"c":[{"v":"Spain","f":null},{"v":1,"f":null}]},
      |  {"c":[{"v":"China","f":null},{"v":1,"f":null}]},
      |  {"c":[{"v":"USA","f":null},{"v":1,"f":null}]},
      |  {"c":[{"v":"Peru","f":null},{"v":1,"f":null}]},
      |  {"c":[{"v":"RU","f":null},{"v":1,"f":null}]}
      |  ]
      |}
    """.stripMargin
  }
}
