package scerde
package ser

trait Serializer {
  type Ok
  type Error

  type SerializeSeq <: ser.SerializeSeq {
    type Ok = Serializer.this.Ok
    type Error = Serializer.this.Error
  }

  type SerializeTuple <: ser.SerializeTuple {
    type Ok = Serializer.this.Ok
    type Error = Serializer.this.Error
  }

  type SerializeTupleStruct <: ser.SerializeTupleStruct {
    type Ok = Serializer.this.Ok
    type Error = Serializer.this.Error
  }

  type SerializeTupleVariant <: ser.SerializeTupleVariant {
    type Ok = Serializer.this.Ok
    type Error = Serializer.this.Error
  }

  type SerializeMap <: ser.SerializeMap {
    type Ok = Serializer.this.Ok
    type Error = Serializer.this.Error
  }

  type SerializeStruct <: ser.SerializeStruct {
    type Ok = Serializer.this.Ok
    type Error = Serializer.this.Error
  }

  type SerializeStructVariant <: ser.SerializeStructVariant {
    type Ok = Serializer.this.Ok
    type Error = Serializer.this.Error
  }

  def serializeBool(v: Boolean): Either[Error, Ok]

  def serializeByte(v: Byte): Either[Error, Ok]

  def serializeShort(v: Short): Either[Error, Ok]

  def serializeInt(v: Int): Either[Error, Ok]

  def serializeLong(v: Long): Either[Error, Ok]

  def serializeUByte(v: Byte): Either[Error, Ok]

  def serializeUShort(v: Short): Either[Error, Ok]

  def serializeUInt(v: Int): Either[Error, Ok]

  def serializeULong(v: Long): Either[Error, Ok]

  def serializeFloat(v: Float): Either[Error, Ok]

  def serializeDouble(v: Double): Either[Error, Ok]

  def serializeChar(v: Char): Either[Error, Ok]

  def serializeString(v: String): Either[Error, Ok]

  def serializeBytes(v: Array[Byte]): Either[Error, Ok]

  def serializeNone(): Either[Error, Ok]

  def serializeSome[T: Serialize](value: T): Either[Error, Ok]

  def serializeUnit(): Either[Error, Ok]

  def serializeUnitStruct(name: String): Either[Error, Ok]

  def serializeUnitVariant(name: String, variantIndex: Int, variant: String): Either[Error, Ok]

  def serializeNewtypeStruct[T: Serialize](name: String, value: T): Either[Error, Ok]

  def serializeNewtypeVariant[T: Serialize](
    name: String,
    variantIndex: Int,
    variant: String,
    value: T,
  ): Either[Error, Ok]

  def serializeSeq(len: Option[Int] = None): Either[Error, SerializeSeq]

  def serializeTuple(len: Int): Either[Error, SerializeTuple]

  def serializeTupleStruct(name: String, len: Int): Either[Error, SerializeTupleStruct]

  def serializeTupleVariant(
    name: String,
    variantIndex: Int,
    variant: String,
    len: Int,
  ): Either[Error, SerializeTupleVariant]

  def serializeMap(len: Option[Int] = None): Either[Error, SerializeMap]

  def serializeStruct(name: String, len: Int): Either[Error, SerializeStruct]

  def serializeStructVariant(
    name: String,
    variantIndex: Int,
    variant: String,
    len: Int,
  ): Either[Error, SerializeStructVariant]
}

trait SerializeSeq {
  type Ok
  type Error

  def serializeElement[T: Serialize](value: T): Either[Error, Unit]

  def done(): Either[Error, Ok]
}

trait SerializeTuple {
  type Ok
  type Error

  def serializeElement[T: Serialize](value: T): Either[Error, Unit]

  def done(): Either[Error, Ok]
}

trait SerializeTupleStruct {
  type Ok
  type Error

  def serializeField[T: Serialize](value: T): Either[Error, Unit]

  def done(): Either[Error, Ok]
}

trait SerializeTupleVariant {
  type Ok
  type Error

  def serializeField[T: Serialize](value: T): Either[Error, Unit]

  def done(): Either[Error, Ok]
}

trait SerializeMap {
  type Ok
  type Error

  def serializeKey[T: Serialize](key: T): Either[Error, Unit]

  def serializeValue[T: Serialize](value: T): Either[Error, Unit]

  def serializeEntry[K: Serialize, V: Serialize](key: K, value: V): Either[Error, Unit] =
    serializeKey(key).flatMap(_ => serializeValue(value))

  def done(): Either[Error, Ok]
}

trait SerializeStruct {
  type Ok
  type Error

  def serializeField[T: Serialize](key: String, value: T): Either[Error, Unit]

  def skipField(key: String): Either[Error, Unit] = {
    val _ = key
    Right(())
  }

  def done(): Either[Error, Ok]
}

trait SerializeStructVariant {
  type Ok
  type Error

  def serializeField[T: Serialize](key: String, value: T): Either[Error, Unit]

  def skipField(key: String): Either[Error, Unit] = {
    val _ = key
    Right(())
  }

  def done(): Either[Error, Ok]
}
