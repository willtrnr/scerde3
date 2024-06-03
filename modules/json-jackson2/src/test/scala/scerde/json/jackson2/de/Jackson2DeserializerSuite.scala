package scerde.json.jackson2
package de

class Jackson2DeserializerSuite extends munit.FunSuite {
  test("deserialize string") {
    val res = Jackson2Deserializer.fromString[String]("\"test\"")
    assertEquals(res, Right("test"))
  }
}
