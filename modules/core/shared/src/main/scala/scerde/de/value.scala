package scerde
package de
package value

abstract private[value] class AnyDeserializer[E] extends Deserializer {
  override type Error = E

  override def deserializeBool[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeByte[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeShort[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeInt[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeLong[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeUByte[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeUShort[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeUInt[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeULong[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeFloat[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeDouble[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeChar[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeString[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeBytes[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeOption[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeUnit[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeUnitStruct[V <: Visitor](name: String, visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeNewtypeStruct[V <: Visitor](name: String, visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeSeq[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeTuple[V <: Visitor](len: Int, visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeTupleStruct[V <: Visitor](name: String, len: Int, visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeMap[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeStruct[V <: Visitor](
    name: String,
    fields: Seq[String],
    visitor: V,
  ): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeEnum[V <: Visitor](
    name: String,
    variants: Seq[String],
    visitor: V,
  ): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeIdentifier[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)

  override def deserializeIgnoredAny[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    this.deserializeAny(visitor)
}

class BoolDeserializer[E: de.Error](value: Boolean) extends AnyDeserializer[E] {
  override def deserializeAny[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    visitor.visitBool(value)
}

class ByteDeserializer[E: de.Error](value: Byte) extends AnyDeserializer[E] {
  override def deserializeAny[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    visitor.visitByte(value)
}

class IntDeserializer[E: de.Error](value: Int) extends AnyDeserializer[E] {
  override def deserializeAny[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    visitor.visitInt(value)
}

class LongDeserializer[E: de.Error](value: Long) extends AnyDeserializer[E] {
  override def deserializeAny[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    visitor.visitLong(value)
}

class FloatDeserializer[E: de.Error](value: Float) extends AnyDeserializer[E] {
  override def deserializeAny[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    visitor.visitFloat(value)
}

class DoubleDeserializer[E: de.Error](value: Double) extends AnyDeserializer[E] {
  override def deserializeAny[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    visitor.visitDouble(value)
}

class CharDeserializer[E: de.Error](value: Char) extends AnyDeserializer[E] {
  override def deserializeAny[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    visitor.visitChar(value)
}

class StringDeserializer[E: de.Error](value: String) extends AnyDeserializer[E] {
  override def deserializeAny[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    visitor.visitString(value)
}

class BytesDeserializer[E: de.Error](value: Array[Byte]) extends AnyDeserializer[E] {
  override def deserializeAny[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    visitor.visitBytes(value)
}

class UnitDeserializer[E: de.Error] extends AnyDeserializer[E] {
  override def deserializeAny[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    visitor.visitUnit()

  override def deserializeOption[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    visitor.visitNone()
}
