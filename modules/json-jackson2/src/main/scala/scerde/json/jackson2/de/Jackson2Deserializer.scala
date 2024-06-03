package scerde.json.jackson2
package de

import java.io.DataInput
import java.io.InputStream
import java.io.Reader

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.{JsonToken => JT}

import scerde.de.Deserialize
import scerde.de.Deserializer
import scerde.de.Error
import scerde.de.MapAccess
import scerde.de.SeqAccess
import scerde.de.Visitor
import scerde.de.value.StringDeserializer
import scerde.json.jackson2.Jackson2Error._

class Jackson2Deserializer(private[de] val inner: JsonParser) extends Deserializer with AutoCloseable {
  override type Error = Jackson2Error

  private[de] def readToken(): Either[Error, JT] =
    catchError(Option(inner.currentToken()).getOrElse(inner.nextToken()))

  private[de] def commitRead(): Either[Error, Unit] =
    if (inner.hasCurrentToken()) {
      for {
        _ <- catchError(inner.finishToken())
        _ <- catchError(inner.clearCurrentToken())
      } yield ()
    } else {
      Right(())
    }

  private[de] def tryCommit[A](block: => Either[Error, A]): Either[Error, A] =
    for {
      a <- block
      _ <- commitRead()
    } yield a

  override def deserializeAny[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    readToken().flatMap {
      case JT.START_OBJECT                 => deserializeMap(visitor)
      case JT.START_ARRAY                  => deserializeSeq(visitor)
      case JT.FIELD_NAME | JT.VALUE_STRING => tryCommit(catchError(inner.getValueAsString()).flatMap(visitor.visitString(_)))
      case JT.VALUE_NUMBER_INT             => tryCommit(catchError(inner.getLongValue()).flatMap(visitor.visitLong(_)))
      case JT.VALUE_NUMBER_FLOAT           => tryCommit(catchError(inner.getDoubleValue()).flatMap(visitor.visitDouble(_)))
      case JT.VALUE_TRUE                   => tryCommit(visitor.visitBool(true))
      case JT.VALUE_FALSE                  => tryCommit(visitor.visitBool(false))
      case JT.VALUE_NULL                   => tryCommit(visitor.visitUnit())
      case t                               => Left(Error[Error].invalidType(t, visitor.expecting))
    }

  override def deserializeBool[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    readToken().flatMap {
      case JT.VALUE_TRUE  => tryCommit(visitor.visitBool(true))
      case JT.VALUE_FALSE => tryCommit(visitor.visitBool(false))
      case t              => Left(Error[Error].invalidType(t, visitor.expecting))
    }

  override def deserializeByte[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    readToken().flatMap {
      case JT.VALUE_NUMBER_INT | JT.VALUE_NUMBER_FLOAT =>
        tryCommit(catchError(inner.getByteValue()).flatMap(visitor.visitByte(_)))
      case t => Left(Error[Error].invalidType(t, visitor.expecting))
    }

  override def deserializeShort[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    readToken().flatMap {
      case JT.VALUE_NUMBER_INT | JT.VALUE_NUMBER_FLOAT =>
        tryCommit(catchError(inner.getShortValue()).flatMap(visitor.visitShort(_)))
      case t => Left(Error[Error].invalidType(t, visitor.expecting))
    }

  override def deserializeInt[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    readToken().flatMap {
      case JT.VALUE_NUMBER_INT | JT.VALUE_NUMBER_FLOAT =>
        tryCommit(catchError(inner.getIntValue()).flatMap(visitor.visitInt(_)))
      case t => Left(Error[Error].invalidType(t, visitor.expecting))
    }

  override def deserializeLong[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    readToken().flatMap {
      case JT.VALUE_NUMBER_INT | JT.VALUE_NUMBER_FLOAT =>
        tryCommit(catchError(inner.getLongValue()).flatMap(visitor.visitLong(_)))
      case t => Left(Error[Error].invalidType(t, visitor.expecting))
    }

  override def deserializeUByte[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    deserializeByte(visitor)

  override def deserializeUShort[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    deserializeShort(visitor)

  override def deserializeUInt[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    deserializeInt(visitor)

  override def deserializeULong[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    deserializeLong(visitor)

  override def deserializeFloat[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    readToken().flatMap {
      case JT.VALUE_NUMBER_INT | JT.VALUE_NUMBER_FLOAT =>
        tryCommit(catchError(inner.getFloatValue()).flatMap(visitor.visitFloat(_)))
      case t => Left(Error[Error].invalidType(t, visitor.expecting))
    }

  override def deserializeDouble[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    readToken().flatMap {
      case JT.VALUE_NUMBER_INT | JT.VALUE_NUMBER_FLOAT =>
        tryCommit(catchError(inner.getDoubleValue()).flatMap(visitor.visitDouble(_)))
      case t => Left(Error[Error].invalidType(t, visitor.expecting))
    }

  override def deserializeChar[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    readToken().flatMap {
      case JT.FIELD_NAME | JT.VALUE_STRING =>
        catchError(inner.getValueAsString()).flatMap { v =>
          v.length match {
            case 1 => tryCommit(visitor.visitChar(v.charAt(0)))
            case n => Left(Error[Error].invalidLength(n, visitor.expecting))
          }
        }
      case t => Left(Error[Error].invalidType(t, visitor.expecting))
    }

  override def deserializeString[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    readToken().flatMap {
      case JT.FIELD_NAME | JT.VALUE_STRING => tryCommit(catchError(inner.getValueAsString()).flatMap(visitor.visitString(_)))
      case t                               => Left(Error[Error].invalidType(t, visitor.expecting))
    }

  override def deserializeBytes[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    readToken().flatMap {
      case JT.VALUE_STRING => tryCommit(catchError(inner.getBinaryValue()).flatMap(visitor.visitBytes(_)))
      case t               => Left(Error[Error].invalidType(t, visitor.expecting))
    }

  override def deserializeOption[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    readToken().flatMap {
      case JT.VALUE_NULL => tryCommit(visitor.visitNone())
      case _             => visitor.visitSome(this)
    }

  override def deserializeUnit[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    readToken().flatMap {
      case JT.VALUE_NULL => tryCommit(visitor.visitUnit())
      case t             => Left(Error[Error].invalidType(t, visitor.expecting))
    }

  override def deserializeUnitStruct[V <: Visitor](name: String, visitor: V): Either[Error, visitor.Value] =
    deserializeAny(visitor)

  override def deserializeNewtypeStruct[V <: Visitor](name: String, visitor: V): Either[Error, visitor.Value] =
    deserializeAny(visitor)

  override def deserializeSeq[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    readToken().flatMap {
      case _ if inner.isExpectedStartArrayToken() =>
        commitRead().flatMap(_ => visitor.visitSeq(new Jackson2SeqAccess(this)))
      case t => Left(Error[Error].invalidType(t, visitor.expecting))
    }

  override def deserializeTuple[V <: Visitor](len: Int, visitor: V): Either[Error, visitor.Value] =
    deserializeSeq(visitor)

  override def deserializeTupleStruct[V <: Visitor](name: String, len: Int, visitor: V): Either[Error, visitor.Value] =
    deserializeSeq(visitor)

  override def deserializeMap[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    readToken().flatMap {
      case _ if inner.isExpectedStartObjectToken() =>
        commitRead().flatMap(_ => visitor.visitMap(new Jackson2MapAccess(this)))
      case t => Left(Error[Error].invalidType(t, visitor.expecting))
    }

  override def deserializeStruct[V <: Visitor](
    name: String,
    fields: Seq[String],
    visitor: V,
  ): Either[Error, visitor.Value] =
    deserializeMap(visitor)

  override def deserializeEnum[V <: Visitor](
    name: String,
    variants: Seq[String],
    visitor: V,
  ): Either[Error, visitor.Value] =
    deserializeMap(visitor)

  override def deserializeIdentifier[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    deserializeString(visitor)

  override def deserializeIgnoredAny[V <: Visitor](visitor: V): Either[Error, visitor.Value] =
    deserializeAny(visitor)

  override def close(): Unit =
    inner.close()
}

object Jackson2Deserializer {
  private[this] val defaultFactory: JsonFactory = new JsonFactory

  @inline def apply(parser: JsonParser): Jackson2Deserializer =
    new Jackson2Deserializer(parser)

  @inline def apply(in: Reader): Jackson2Deserializer =
    apply(defaultFactory.createParser(in))

  @inline def apply(in: DataInput): Jackson2Deserializer =
    apply(defaultFactory.createParser(in))

  @inline def apply(in: InputStream): Jackson2Deserializer =
    apply(defaultFactory.createParser(in))

  def deserialize[A: Deserialize](in: JsonParser): Either[Jackson2Error, A] = {
    val deserializer = apply(in)
    for {
      a <- Deserialize[A].deserialize(deserializer)
      _ <- catchError(deserializer.close())
    } yield a
  }

  @inline def deserialize[A: Deserialize](in: Reader): Either[Jackson2Error, A] =
    deserialize(defaultFactory.createParser(in))

  @inline def deserialize[A: Deserialize](in: DataInput): Either[Jackson2Error, A] =
    deserialize(defaultFactory.createParser(in))

  @inline def deserialize[A: Deserialize](in: InputStream): Either[Jackson2Error, A] =
    deserialize(defaultFactory.createParser(in))

  @inline def fromString[A: Deserialize](data: String): Either[Jackson2Error, A] =
    deserialize(defaultFactory.createParser(data))

  @inline def fromBytes[A: Deserialize](data: Array[Byte]): Either[Jackson2Error, A] =
    deserialize(defaultFactory.createParser(data))
}

final private class Jackson2SeqAccess(deserializer: Jackson2Deserializer) extends SeqAccess {
  override type Error = Jackson2Error

  override def nextElement[A: Deserialize](): Either[Error, Option[A]] =
    deserializer.readToken().flatMap {
      case JT.END_ARRAY => deserializer.commitRead().map(_ => None)
      case _            => Deserialize[A].deserialize(deserializer).map(Some(_))
    }
}

final private class Jackson2MapAccess(deserializer: Jackson2Deserializer) extends MapAccess {
  override type Error = Jackson2Error

  override def nextKey[K: Deserialize](): Either[Error, Option[K]] =
    deserializer.readToken().flatMap {
      case JT.FIELD_NAME =>
        for {
          n <- catchError(deserializer.inner.getValueAsString())
          v <- deserializer.tryCommit(Deserialize[K].deserialize(new StringDeserializer[Error](n)))
        } yield Some(v)
      case JT.END_OBJECT => deserializer.commitRead().map(_ => None)
      case t             => Left(Error[Error].invalidType(t, "field name"))
    }

  override def nextValue[V: Deserialize](): Either[Error, V] =
    Deserialize[V].deserialize(deserializer)
}
