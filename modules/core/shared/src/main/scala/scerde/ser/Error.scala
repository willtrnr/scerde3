package scerde
package ser

trait Error[E] {
  def custom(msg: String): E
}

object Error extends ErrorInstances {
  @inline def apply[E](implicit ev: Error[E]): Error[E] = ev
}

sealed abstract private[ser] class ErrorInstances {
  implicit val errorString: Error[String] = new Error[String] {
    @inline override def custom(msg: String): String = msg
  }
}
