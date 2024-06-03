package scerde
package de

trait Deserialize[A] {
  def deserialize[D <: Deserializer](de: D): Either[de.Error, A]
}

object Deserialize extends DeserializeInstances with PlatformDeserialize {
  @inline def apply[A](implicit ev: Deserialize[A]): Deserialize[A] = ev
}

sealed abstract private[de] class DeserializeInstances {
  implicit val deserializeBool: Deserialize[Boolean] = new Deserialize[Boolean] {
    final val boolVisitor = new Visitor {
      type Value = Boolean

      override def expecting: String = "bool"

      override def visitBool[E: Error](v: Boolean): Either[E, Value] = Right(v)
    }

    @inline override def deserialize[D <: Deserializer](de: D): Either[de.Error, Boolean] =
      de.deserializeBool(boolVisitor)
  }

  implicit val deserializeByte: Deserialize[Byte] = new Deserialize[Byte] {
    final val byteVisitor = new Visitor {
      type Value = Byte

      override def expecting: String = "byte"

      override def visitByte[E: Error](v: Byte): Either[E, Value] = Right(v)
    }

    @inline override def deserialize[D <: Deserializer](de: D): Either[de.Error, Byte] =
      de.deserializeByte(byteVisitor)
  }

  implicit val deserializeShort: Deserialize[Short] = new Deserialize[Short] {
    final val shortVisitor = new Visitor {
      type Value = Short

      override def expecting: String = "short"

      override def visitShort[E: Error](v: Short): Either[E, Value] = Right(v)
    }

    @inline override def deserialize[D <: Deserializer](de: D): Either[de.Error, Short] =
      de.deserializeShort(shortVisitor)
  }

  implicit val deserializeInt: Deserialize[Int] = new Deserialize[Int] {
    final val intVisitor = new Visitor {
      type Value = Int

      override def expecting: String = "int"

      override def visitInt[E: Error](v: Int): Either[E, Value] = Right(v)
    }

    @inline override def deserialize[D <: Deserializer](de: D): Either[de.Error, Int] =
      de.deserializeInt(intVisitor)
  }

  implicit val deserializeLong: Deserialize[Long] = new Deserialize[Long] {
    final val longVisitor = new Visitor {
      type Value = Long

      override def expecting: String = "long"

      override def visitLong[E: Error](v: Long): Either[E, Value] = Right(v)
    }

    @inline override def deserialize[D <: Deserializer](de: D): Either[de.Error, Long] =
      de.deserializeLong(longVisitor)
  }

  implicit val deserializeFloat: Deserialize[Float] = new Deserialize[Float] {
    final val floatVisitor = new Visitor {
      type Value = Float

      override def expecting: String = "float"

      override def visitFloat[E: Error](v: Float): Either[E, Value] = Right(v)
    }

    @inline override def deserialize[D <: Deserializer](de: D): Either[de.Error, Float] =
      de.deserializeFloat(floatVisitor)
  }

  implicit val deserializeDouble: Deserialize[Double] = new Deserialize[Double] {
    final val doubleVisitor = new Visitor {
      type Value = Double

      override def expecting: String = "double"

      override def visitDouble[E: Error](v: Double): Either[E, Value] = Right(v)
    }

    @inline override def deserialize[D <: Deserializer](de: D): Either[de.Error, Double] =
      de.deserializeDouble(doubleVisitor)
  }

  implicit val deserializeChar: Deserialize[Char] = new Deserialize[Char] {
    final val charVisitor = new Visitor {
      type Value = Char

      override def expecting: String = "char"

      override def visitChar[E: Error](v: Char): Either[E, Value] = Right(v)
    }

    @inline override def deserialize[D <: Deserializer](de: D): Either[de.Error, Char] =
      de.deserializeChar(charVisitor)
  }

  implicit val deserializeString: Deserialize[String] = new Deserialize[String] {
    final val stringVisitor = new Visitor {
      type Value = String

      override def expecting: String = "string"

      override def visitString[E: Error](v: String): Either[E, Value] = Right(v)
    }

    @inline override def deserialize[D <: Deserializer](de: D): Either[de.Error, String] =
      de.deserializeString(stringVisitor)
  }

  implicit val deserializeBytes: Deserialize[Array[Byte]] = new Deserialize[Array[Byte]] {
    final val bytesVisitor = new Visitor {
      type Value = Array[Byte]

      override def expecting: String = "byte array"

      override def visitBytes[E: Error](v: Array[Byte]): Either[E, Value] = Right(v)
    }

    @inline override def deserialize[D <: Deserializer](de: D): Either[de.Error, Array[Byte]] =
      de.deserializeString(bytesVisitor)
  }

  implicit def deserializeOption[A](implicit A: Deserialize[A]): Deserialize[Option[A]] = new Deserialize[Option[A]] {
    final val optionVisitor = new Visitor {
      type Value = Option[A]

      override def expecting: String = "option"

      override def visitNone[E: Error](): Either[E, Value] = Right(None)

      override def visitSome[D <: Deserializer](de: D)(implicit E: Error[de.Error]): Either[de.Error, Value] =
        A.deserialize(de).map(Some.apply)
    }

    @inline override def deserialize[D <: Deserializer](de: D): Either[de.Error, Option[A]] =
      de.deserializeOption(optionVisitor)
  }

  implicit val deserializeUnit: Deserialize[Unit] = new Deserialize[Unit] {
    final val unitVisitor = new Visitor {
      type Value = Unit

      override def expecting: String = "unit"

      override def visitUnit[E: Error](): Either[E, Value] = Right(())
    }

    @inline override def deserialize[D <: Deserializer](de: D): Either[de.Error, Unit] =
      de.deserializeUnit(unitVisitor)
  }
}
