package scerde.json.jackson2

import java.io.IOException

class Jackson2Error(val self: IOException) extends AnyVal

object Jackson2Error {
  private[jackson2] def catchError[A](block: => A): Either[Jackson2Error, A] =
    try {
      Right(block)
    } catch {
      case e: IOException => Left(new Jackson2Error(e))
      case e: Exception   => Left(new Jackson2Error(new IOException(e)))
    }

  implicit val serError: scerde.ser.Error[Jackson2Error] = new scerde.ser.Error[Jackson2Error] {
    @inline override def custom(msg: String): Jackson2Error =
      new Jackson2Error(new IOException(msg))
  }

  implicit val deError: scerde.de.Error[Jackson2Error] = new scerde.de.Error[Jackson2Error] {
    @inline override def custom(msg: String): Jackson2Error =
      new Jackson2Error(new IOException(msg))
  }
}
