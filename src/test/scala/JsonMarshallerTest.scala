import org.scalatest.FunSuite

class JsonMarshallerTest extends FunSuite {

  test("testShow") {
    val a = new JsonMarshaller
    a.show
  }

}
