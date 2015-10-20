import MDM_DataProvider.{Row, Col}
import spray.json._
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

/*
Reference for Google Chart Data Structure
  https://developers.google.com/chart/interactive/docs/reference#DataTable
*/

object GoogleChartsDataProvider extends GC_DataJsonProtocol {

  def gc_data(cols: List[Col], rows: List[Row]) = {
    val spalten = createCols(cols)
    val zeilen = createRows(rows)
    GC_Data(spalten, zeilen).toJson
  }

  def createCols(cols: List[Col]) = {

    cols.zipWithIndex.map( a => GC_Col(Some("col_"+a._2), a._1.label, a._1.pattern, a._1.gc_type ) )
//    var columns = new ListBuffer[GC_Col]
//    var i = 0
//    for (a <- cols) {
//      i += 1
//      columns += GC_Col(Some("col_" + i), a.label, a.pattern, a.gc_type)
//    }
//    columns.toList
  }

  def createRows(rows: List[Row]) = {
    rows.zipWithIndex.map( a => GC_Row(List(GC_Cell(a._1.country, None, None), GC_Cell(a._1.value.toString, None, None))) )
//    var zeilen = new ListBuffer[GC_Row]
//    for (a <- rows.toList) {
//      zeilen += GC_Row(List(GC_Cell(a.country, None, None), GC_Cell(a.value.toString, None, None)))
//    }
//    zeilen.toList
  }

}
