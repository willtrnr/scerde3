package scerde
package de

trait Visitor {
  type Value

  def expecting: String

  def visitBool[E: Error](v: Boolean): Either[E, Value] =
    Left(Error[E].invalidType(v, this.expecting))

  def visitByte[E: Error](v: Byte): Either[E, Value] =
    this.visitLong(v.toLong)

  def visitShort[E: Error](v: Short): Either[E, Value] =
    this.visitLong(v.toLong)

  def visitInt[E: Error](v: Int): Either[E, Value] =
    this.visitLong(v.toLong)

  def visitLong[E: Error](v: Long): Either[E, Value] =
    Left(Error[E].invalidType(v, this.expecting))

  def visitUByte[E: Error](v: Byte): Either[E, Value] =
    this.visitULong(v.toLong)

  def visitUShort[E: Error](v: Short): Either[E, Value] =
    this.visitULong(v.toLong)

  def visitUInt[E: Error](v: Int): Either[E, Value] =
    this.visitULong(v.toLong)

  def visitULong[E: Error](v: Long): Either[E, Value] =
    Left(Error[E].invalidType(v, this.expecting))

  def visitFloat[E: Error](v: Float): Either[E, Value] =
    this.visitDouble(v.toDouble)

  def visitDouble[E: Error](v: Double): Either[E, Value] =
    Left(Error[E].invalidType(v, this.expecting))

  def visitChar[E: Error](v: Char): Either[E, Value] =
    this.visitString(v.toString)

  def visitString[E: Error](v: String): Either[E, Value] =
    Left(Error[E].invalidType(v, this.expecting))

  def visitBytes[E: Error](v: Array[Byte]): Either[E, Value] = {
    val _ = v
    Left(Error[E].invalidType("byte array", this.expecting))
  }

  def visitNone[E: Error](): Either[E, Value] =
    Left(Error[E].invalidType("Option value", this.expecting))

  def visitSome[D <: Deserializer](d: D)(implicit E: Error[d.Error]): Either[d.Error, Value] =
    Left(Error[d.Error].invalidType("Option value", this.expecting))

  def visitUnit[E: Error](): Either[E, Value] =
    Left(Error[E].invalidType("unit value", this.expecting))

  def visitSeq[A <: SeqAccess](a: A)(implicit E: Error[a.Error]): Either[a.Error, Value] =
    Left(Error[a.Error].invalidType("sequence", this.expecting))

  def visitMap[A <: MapAccess](a: A)(implicit E: Error[a.Error]): Either[a.Error, Value] =
    Left(Error[a.Error].invalidType("map", this.expecting))

  def visitEnum[A <: EnumAccess](a: A)(implicit E: Error[a.Error]): Either[a.Error, Value] =
    Left(Error[a.Error].invalidType("enum", this.expecting))
}

trait SeqAccess {
  type Error

  def nextElement[A: Deserialize](): Either[Error, Option[A]]
}

trait MapAccess {
  type Error

  def nextKey[K: Deserialize](): Either[Error, Option[K]]

  def nextValue[V: Deserialize](): Either[Error, V]

  def nextEntry[K: Deserialize, V: Deserialize](): Either[Error, Option[(K, V)]] =
    this.nextKey[K]() match {
      case Right(Some(k)) => this.nextValue[V]().map(v => Some((k, v)))
      case Right(None)    => Right(None)
      case Left(e)        => Left(e)
    }
}

trait EnumAccess {
  type Error

  type Variant <: VariantAccess

  def variant[V: Deserialize](): Either[Error, (V, Variant)]
}

trait VariantAccess {
  type Error

  def unitVariant(): Either[Error, Unit]

  def tupleVariant[V <: Visitor](len: Int, visitor: V): Either[Error, visitor.Value]

  def structVariant[V <: Visitor](fields: Seq[String], visitor: V): Either[Error, visitor.Value]
}
