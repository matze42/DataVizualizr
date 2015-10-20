import MDM_DataProvider.{Row, Col}
import spray.json._
import scala.collection.mutable.ListBuffer

case class GC_Col(id: Option[String], label: Option[String], pattern: Option[String], `type`: String)

case class GC_Row(c: List[GC_Cell])

case class GC_Cell(v: String, f: Option[String], p: Option[String])

case class GC_Data(cols: List[GC_Col], rows: List[GC_Row], p: Option[String])

trait GC_DataJsonProtocol extends DefaultJsonProtocol {
  implicit val formatGC_Cell = jsonFormat3(GC_Cell.apply)
  implicit val formatGC_Row = jsonFormat1(GC_Row.apply)
  implicit val formatGC_Col = jsonFormat4(GC_Col.apply)
  implicit val formatGC_Data = jsonFormat3(GC_Data.apply)
}

/**
  * Reference for Google Chart Data Structure
  * https://developers.google.com/chart/interactive/docs/reference#DataTable
  */
object GoogleChartsDataProvider extends GC_DataJsonProtocol {

  def gc_data(cols: List[Col], rows: List[Row]) = {
    val spalten = createCols(cols)
    val zeilen = createRows(rows)
    GC_Data(spalten, zeilen, None).toJson
  }

  def createCols(cols: List[Col]) = {
    cols.zipWithIndex.map(a => GC_Col(Some("col_" + a._2), a._1.label, a._1.pattern, a._1.gc_type))
  }

  def createRows(rows: List[Row]) = {
    rows.zipWithIndex.map(a => GC_Row(List(GC_Cell(a._1.country, None, None), GC_Cell(a._1.value.toString, None, None))))
  }

}
