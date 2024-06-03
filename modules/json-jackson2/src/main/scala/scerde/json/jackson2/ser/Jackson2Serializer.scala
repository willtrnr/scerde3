package scerde.json.jackson2
package ser

import java.io.ByteArrayOutputStream
import java.io.DataOutput
import java.io.OutputStream
import java.io.StringWriter
import java.io.Writer

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator

import scerde.json.jackson2.Jackson2Error._
import scerde.ser.Error
import scerde.ser.Serialize
import scerde.ser.SerializeMap
import scerde.ser.SerializeSeq
import scerde.ser.SerializeStruct
import scerde.ser.SerializeStructVariant
import scerde.ser.SerializeTuple
import scerde.ser.SerializeTupleStruct
import scerde.ser.SerializeTupleVariant
import scerde.ser.Serializer

class Jackson2Serializer(private[ser] val inner: JsonGenerator) extends Serializer with AutoCloseable {
  override type Ok = Unit
  override type Error = Jackson2Error

  type SerializeSeq = Jackson2SerializeSeqLike
  type SerializeTuple = Jackson2SerializeSeqLike
  type SerializeTupleStruct = Jackson2SerializeSeqLike
  type SerializeTupleVariant = Jackson2SerializeSeqLike
  type SerializeMap = Jackson2SerializeMapLike
  type SerializeStruct = Jackson2SerializeMapLike
  type SerializeStructVariant = Jackson2SerializeMapLike

  override def serializeBool(v: Boolean): Either[Error, Ok] =
    catchError(inner.writeBoolean(v))

  override def serializeByte(v: Byte): Either[Error, Ok] =
    catchError(inner.writeNumber(v.toShort))

  override def serializeShort(v: Short): Either[Error, Ok] =
    catchError(inner.writeNumber(v))

  override def serializeInt(v: Int): Either[Error, Ok] =
    catchError(inner.writeNumber(v))

  override def serializeLong(v: Long): Either[Error, Ok] =
    catchError(inner.writeNumber(v))

  override def serializeUByte(v: Byte): Either[Error, Ok] =
    catchError(inner.writeNumber(v.toShort))

  override def serializeUShort(v: Short): Either[Error, Ok] =
    catchError(inner.writeNumber(v))

  override def serializeUInt(v: Int): Either[Error, Ok] =
    catchError(inner.writeNumber(v))

  override def serializeULong(v: Long): Either[Error, Ok] =
    catchError(inner.writeNumber(v))

  override def serializeFloat(v: Float): Either[Error, Ok] =
    catchError(inner.writeNumber(v))

  override def serializeDouble(v: Double): Either[Error, Ok] =
    catchError(inner.writeNumber(v))

  override def serializeChar(v: Char): Either[Error, Ok] =
    catchError(inner.writeString(v.toString()))

  override def serializeString(v: String): Either[Error, Ok] =
    catchError(inner.writeString(v))

  override def serializeBytes(v: Array[Byte]): Either[Error, Ok] =
    catchError(inner.writeBinary(v))

  override def serializeNone(): Either[Error, Ok] =
    catchError(inner.writeNull())

  override def serializeSome[T: Serialize](value: T): Either[Error, Ok] =
    Serialize[T].serialize(value, this)

  override def serializeUnit(): Either[Error, Ok] =
    catchError(inner.writeNull())

  override def serializeUnitStruct(name: String): Either[Error, Ok] =
    catchError(inner.writeString(name))

  override def serializeUnitVariant(name: String, variantIndex: Int, variant: String): Either[Error, Ok] =
    catchError(inner.writeString(variant))

  override def serializeNewtypeStruct[T: Serialize](name: String, value: T): Either[Error, Ok] =
    Serialize[T].serialize(value, this)

  override def serializeNewtypeVariant[T: Serialize](
    name: String,
    variantIndex: Int,
    variant: String,
    value: T,
  ): Either[Error, Ok] =
    this.serializeNewtypeStruct(variant, value)

  override def serializeSeq(len: Option[Int] = None): Either[Error, SerializeSeq] =
    catchError(inner.writeStartArray()).map(_ => new Jackson2SerializeSeqLike(this))

  override def serializeTuple(len: Int): Either[Error, SerializeTuple] =
    catchError(inner.writeStartArray()).map(_ => new Jackson2SerializeSeqLike(this))

  override def serializeTupleStruct(name: String, len: Int): Either[Error, SerializeTupleStruct] =
    catchError(inner.writeStartArray()).map(_ => new Jackson2SerializeSeqLike(this))

  override def serializeTupleVariant(
    name: String,
    variantIndex: Int,
    variant: String,
    len: Int,
  ): Either[Error, SerializeTupleVariant] =
    for {
      _ <- catchError(inner.writeStartObject())
      _ <- catchError(inner.writeArrayFieldStart(variant))
    } yield new Jackson2SerializeSeqLike(this, () => catchError(inner.writeEndArray()))

  override def serializeMap(len: Option[Int] = None): Either[Error, SerializeMap] =
    catchError(inner.writeStartObject()).map(_ => new Jackson2SerializeMapLike(this))

  override def serializeStruct(name: String, len: Int): Either[Error, SerializeStruct] =
    catchError(inner.writeStartObject()).map(_ => new Jackson2SerializeMapLike(this))

  override def serializeStructVariant(
    name: String,
    variantIndex: Int,
    variant: String,
    len: Int,
  ): Either[Error, SerializeStructVariant] =
    for {
      _ <- catchError(inner.writeStartObject())
      _ <- catchError(inner.writeObjectFieldStart(variant))
    } yield new Jackson2SerializeMapLike(this, () => catchError(inner.writeEndObject()))

  override def close(): Unit =
    inner.close()
}

object Jackson2Serializer {
  private[this] val defaultFactory: JsonFactory = new JsonFactory

  @inline def apply(generator: JsonGenerator): Jackson2Serializer =
    new Jackson2Serializer(generator)

  @inline def apply(out: Writer): Jackson2Serializer =
    apply(defaultFactory.createGenerator(out))

  @inline def apply(out: DataOutput): Jackson2Serializer =
    apply(defaultFactory.createGenerator(out))

  @inline def apply(out: OutputStream): Jackson2Serializer =
    apply(defaultFactory.createGenerator(out))

  def serialize[A: Serialize](out: JsonGenerator)(data: A): Either[Jackson2Error, Unit] = {
    val serializer = apply(out)
    Serialize[A].serialize(data, serializer).flatMap(_ => catchError(serializer.close()))
  }

  @inline def serialize[A: Serialize](out: Writer)(data: A): Either[Jackson2Error, Unit] =
    serialize(defaultFactory.createGenerator(out))(data)

  @inline def serialize[A: Serialize](out: DataOutput)(data: A): Either[Jackson2Error, Unit] =
    serialize(defaultFactory.createGenerator(out))(data)

  @inline def serialize[A: Serialize](out: OutputStream)(data: A): Either[Jackson2Error, Unit] =
    serialize(defaultFactory.createGenerator(out))(data)

  def toString[A: Serialize](data: A): Either[Jackson2Error, String] = {
    val buf = new StringWriter(1024)
    val serializer = apply(buf)
    for {
      _ <- Serialize[A].serialize(data, serializer)
      _ <- catchError(serializer.close())
    } yield buf.toString()
  }

  def toBytes[A: Serialize](data: A): Either[Jackson2Error, Array[Byte]] = {
    val buf = new ByteArrayOutputStream(1024)
    val serializer = apply(buf)
    for {
      _ <- Serialize[A].serialize(data, serializer)
      _ <- catchError(serializer.close())
    } yield buf.toByteArray()
  }
}

final private[ser] class Jackson2SerializeSeqLike(
  serializer: Jackson2Serializer,
  finalize: () => Either[Jackson2Error, Unit] = () => Right(()),
) extends SerializeSeq
    with SerializeTuple
    with SerializeTupleStruct
    with SerializeTupleVariant {
  override type Ok = Unit
  override type Error = Jackson2Error

  override def serializeElement[T: Serialize](value: T): Either[Error, Unit] =
    Serialize[T].serialize(value, serializer)

  override def serializeField[T: Serialize](value: T): Either[Error, Unit] =
    Serialize[T].serialize(value, serializer)

  override def done(): Either[Error, Ok] =
    for {
      _ <- catchError(serializer.inner.writeEndArray())
      _ <- finalize()
    } yield ()
}

final private[ser] class Jackson2SerializeMapLike(
  serializer: Jackson2Serializer,
  finalize: () => Either[Jackson2Error, Unit] = () => Right(()),
) extends SerializeMap
    with SerializeStruct
    with SerializeStructVariant {
  override type Ok = Unit
  override type Error = Jackson2Error

  override def serializeKey[T: Serialize](key: T): Either[Error, Unit] =
    Serialize[T].serialize(key, new MapKeySerializer(serializer))

  override def serializeValue[T: Serialize](value: T): Either[Error, Unit] =
    Serialize[T].serialize(value, serializer)

  override def serializeField[T: Serialize](key: String, value: T): Either[Error, Unit] =
    for {
      _ <- catchError(serializer.inner.writeFieldName(key))
      _ <- Serialize[T].serialize(value, serializer)
    } yield ()

  override def skipField(key: String): Either[Error, Unit] =
    Right(())

  override def done(): Either[Error, Ok] =
    for {
      _ <- catchError(serializer.inner.writeEndObject())
      _ <- finalize()
    } yield ()
}

final private class MapKeySerializer(serializer: Jackson2Serializer) extends Serializer {
  override type Ok = Unit
  override type Error = Jackson2Error

  type SerializeSeq = Nothing
  type SerializeTuple = Nothing
  type SerializeTupleStruct = Nothing
  type SerializeTupleVariant = Nothing
  type SerializeMap = Nothing
  type SerializeStruct = Nothing
  type SerializeStructVariant = Nothing

  @inline private[this] def expectedStringKey[A]: Either[Error, Nothing] =
    Left(Error[Error].custom("expected string key"))

  def serializeBool(v: Boolean): Either[Error, Ok] =
    this.serializeString(v.toString)

  def serializeByte(v: Byte): Either[Error, Ok] =
    this.serializeString(v.toString)

  def serializeShort(v: Short): Either[Error, Ok] =
    this.serializeString(v.toString)

  def serializeInt(v: Int): Either[Error, Ok] =
    this.serializeString(v.toString)

  def serializeLong(v: Long): Either[Error, Ok] =
    this.serializeString(v.toString)

  def serializeUByte(v: Byte): Either[Error, Ok] =
    this.serializeString(v.toString)

  def serializeUShort(v: Short): Either[Error, Ok] =
    this.serializeString(v.toString)

  def serializeUInt(v: Int): Either[Error, Ok] =
    this.serializeString(v.toString)

  def serializeULong(v: Long): Either[Error, Ok] =
    this.serializeString(v.toString)

  def serializeFloat(v: Float): Either[Error, Ok] =
    this.serializeString(v.toString)

  def serializeDouble(v: Double): Either[Error, Ok] =
    this.serializeString(v.toString)

  def serializeChar(v: Char): Either[Error, Ok] =
    this.serializeString(v.toString)

  @inline def serializeString(v: String): Either[Error, Ok] =
    catchError(this.serializer.inner.writeFieldName(v))

  def serializeBytes(v: Array[Byte]): Either[Error, Ok] =
    this.expectedStringKey

  def serializeNone(): Either[Error, Ok] =
    this.expectedStringKey

  def serializeSome[T: Serialize](value: T): Either[Error, Ok] =
    this.expectedStringKey

  def serializeUnit(): Either[Error, Ok] =
    this.expectedStringKey

  def serializeUnitStruct(name: String): Either[Error, Ok] =
    this.expectedStringKey

  def serializeUnitVariant(name: String, variantIndex: Int, variant: String): Either[Error, Ok] =
    this.serializeString(variant)

  def serializeNewtypeStruct[T: Serialize](name: String, value: T): Either[Error, Ok] =
    Serialize[T].serialize(value, this)

  def serializeNewtypeVariant[T: Serialize](
    name: String,
    variantIndex: Int,
    variant: String,
    value: T,
  ): Either[Error, Ok] =
    this.expectedStringKey

  def serializeSeq(len: Option[Int] = None): Either[Error, SerializeSeq] =
    this.expectedStringKey

  def serializeTuple(len: Int): Either[Error, SerializeTuple] =
    this.expectedStringKey

  def serializeTupleStruct(name: String, len: Int): Either[Error, SerializeTupleStruct] =
    this.expectedStringKey

  def serializeTupleVariant(
    name: String,
    variantIndex: Int,
    variant: String,
    len: Int,
  ): Either[Error, SerializeTupleVariant] =
    this.expectedStringKey

  def serializeMap(len: Option[Int] = None): Either[Error, SerializeMap] =
    this.expectedStringKey

  def serializeStruct(name: String, len: Int): Either[Error, SerializeStruct] =
    this.expectedStringKey

  def serializeStructVariant(
    name: String,
    variantIndex: Int,
    variant: String,
    len: Int,
  ): Either[Error, SerializeStructVariant] =
    this.expectedStringKey
}
