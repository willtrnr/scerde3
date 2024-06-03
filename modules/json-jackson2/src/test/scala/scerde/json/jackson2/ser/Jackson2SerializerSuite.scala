package scerde.json.jackson2
package ser

class Jackson2SerializerSuite extends munit.FunSuite {
  test("serialize string") {
    val res = Jackson2Serializer.toString("test")
    assertEquals(res, Right("\"test\""))
  }
}
