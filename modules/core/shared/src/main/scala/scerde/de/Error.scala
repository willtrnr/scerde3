package scerde
package de

trait Error[E] {
  def custom(msg: String): E

  def invalidType(unexp: Any, exp: String): E =
    this.custom(s"invalid type: $unexp, expected: $exp")

  def invalidValue(unexp: Any, exp: String): E =
    this.custom(s"invalid value: $unexp, expected: $exp")

  def invalidLength(len: Int, exp: String): E =
    this.custom(s"invalid length: $len, expected: $exp")
}

object Error extends ErrorInstances {
  @inline def apply[E](implicit ev: Error[E]): Error[E] = ev
}

sealed abstract private[de] class ErrorInstances {
  implicit val errorString: Error[String] = new Error[String] {
    @inline override def custom(msg: String): String = msg
  }
}
