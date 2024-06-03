package scerde
package ser

trait Serialize[A] {
  def serialize[S <: Serializer](value: A, ser: S): Either[ser.Error, ser.Ok]
}

object Serialize extends SerializeInstances with PlatformSerialize {
  @inline def apply[A](implicit ev: Serialize[A]): Serialize[A] = ev
}

sealed abstract private[ser] class SerializeInstances {
  implicit val serializeBoolean: Serialize[Boolean] = new Serialize[Boolean] {
    override def serialize[S <: Serializer](value: Boolean, ser: S): Either[ser.Error, ser.Ok] =
      ser.serializeBool(value)
  }

  implicit val serializeByte: Serialize[Byte] = new Serialize[Byte] {
    override def serialize[S <: Serializer](value: Byte, ser: S): Either[ser.Error, ser.Ok] =
      ser.serializeByte(value)
  }

  implicit val serializeShort: Serialize[Short] = new Serialize[Short] {
    override def serialize[S <: Serializer](value: Short, ser: S): Either[ser.Error, ser.Ok] =
      ser.serializeShort(value)
  }

  implicit val serializeInt: Serialize[Int] = new Serialize[Int] {
    override def serialize[S <: Serializer](value: Int, ser: S): Either[ser.Error, ser.Ok] =
      ser.serializeInt(value)
  }

  implicit val serializeLong: Serialize[Long] = new Serialize[Long] {
    override def serialize[S <: Serializer](value: Long, ser: S): Either[ser.Error, ser.Ok] =
      ser.serializeLong(value)
  }

  implicit val serializeFloat: Serialize[Float] = new Serialize[Float] {
    override def serialize[S <: Serializer](value: Float, ser: S): Either[ser.Error, ser.Ok] =
      ser.serializeFloat(value)
  }

  implicit val serializeDouble: Serialize[Double] = new Serialize[Double] {
    override def serialize[S <: Serializer](value: Double, ser: S): Either[ser.Error, ser.Ok] =
      ser.serializeDouble(value)
  }

  implicit val serializeChar: Serialize[Char] = new Serialize[Char] {
    override def serialize[S <: Serializer](value: Char, ser: S): Either[ser.Error, ser.Ok] =
      ser.serializeChar(value)
  }

  implicit val serializeString: Serialize[String] = new Serialize[String] {
    override def serialize[S <: Serializer](value: String, ser: S): Either[ser.Error, ser.Ok] =
      ser.serializeString(value)
  }

  implicit val serializeBytes: Serialize[Array[Byte]] = new Serialize[Array[Byte]] {
    override def serialize[S <: Serializer](value: Array[Byte], ser: S): Either[ser.Error, ser.Ok] =
      ser.serializeBytes(value)
  }

  implicit def serializeOption[A](implicit A: Serialize[A]): Serialize[Option[A]] = new Serialize[Option[A]] {
    override def serialize[S <: Serializer](value: Option[A], ser: S): Either[ser.Error, ser.Ok] =
      value.fold(ser.serializeNone())(ser.serializeSome(_))
  }

  implicit val serializeUnit: Serialize[Unit] = new Serialize[Unit] {
    override def serialize[S <: Serializer](value: Unit, ser: S): Either[ser.Error, ser.Ok] =
      ser.serializeUnit()
  }

  implicit def serializeSeq[A](implicit A: Serialize[A]): Serialize[Seq[A]] = new Serialize[Seq[A]] {
    override def serialize[S <: Serializer](value: Seq[A], ser: S): Either[ser.Error, ser.Ok] =
      for {
        s <- ser.serializeSeq(Some(value.size))
        _ <- value.foldLeft[Either[ser.Error, Unit]](Right(())) { (a, e) =>
          a.flatMap(_ => s.serializeElement(e))
        }
        o <- s.done()
      } yield o
  }

  implicit def serializeMap[K, V](implicit K: Serialize[K], V: Serialize[V]): Serialize[Map[K, V]] = new Serialize[Map[K, V]] {
    override def serialize[S <: Serializer](value: Map[K, V], ser: S): Either[ser.Error, ser.Ok] =
      for {
        s <- ser.serializeMap(Some(value.size))
        _ <- value.foldLeft[Either[ser.Error, Unit]](Right(())) { (a, kv) =>
          a.flatMap(_ => s.serializeEntry(kv._1, kv._2))
        }
        o <- s.done()
      } yield o
  }
}
