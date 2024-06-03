package scerde.json

import scala.collection.{immutable => imm}

import scerde.de.Deserialize
import scerde.de.Deserializer
import scerde.de.Error
import scerde.de.MapAccess
import scerde.de.SeqAccess
import scerde.de.Visitor
import scerde.ser.Serialize
import scerde.ser.Serializer

sealed abstract class Value extends Product with Serializable

object Value extends ValueInstances {

  case object JNull extends Value
  final case class JBoolean(value: Boolean) extends Value
  final case class JNumber(value: Either[Long, Double]) extends Value
  final case class JString(value: String) extends Value
  final case class JArray(value: imm.Seq[Value]) extends Value
  final case class JObject(value: imm.Map[String, Value]) extends Value

}

sealed abstract class ValueInstances {

  implicit val serializeValue: Serialize[Value] = new Serialize[Value] {
    override def serialize[S <: Serializer](value: Value, ser: S): Either[ser.Error, ser.Ok] =
      value match {
        case Value.JNull             => ser.serializeUnit()
        case Value.JBoolean(v)       => ser.serializeBool(v)
        case Value.JNumber(Left(v))  => ser.serializeLong(v)
        case Value.JNumber(Right(v)) => ser.serializeDouble(v)
        case Value.JString(v)        => ser.serializeString(v)
        case Value.JArray(v) => {
          for {
            s <- ser.serializeSeq(Some(v.size))
            _ <- v.foldLeft(Right(()): Either[ser.Error, Unit]) { (a, i) =>
              a.flatMap(_ => s.serializeElement(i)(serializeValue))
            }
            o <- s.done()
          } yield o
        }
        case Value.JObject(v) =>
          for {
            m <- ser.serializeMap(Some(v.size))
            _ <- v.foldLeft(Right(()): Either[ser.Error, Unit]) { (a, i) =>
              a.flatMap(_ => m.serializeEntry(i._1, i._2)(implicitly[Serialize[String]], serializeValue))
            }
            o <- m.done()
          } yield o
      }
  }

  implicit val deserializeValue: Deserialize[Value] = new Deserialize[Value] {
    final val valueVisitor = new Visitor {
      override type Value = scerde.json.Value

      override def expecting: String = "json value"

      override def visitBool[E: Error](v: Boolean): Either[E, Value] =
        Right(Value.JBoolean(v))

      override def visitLong[E: Error](v: Long): Either[E, Value] =
        Right(Value.JNumber(Left(v)))

      override def visitULong[E: Error](v: Long): Either[E, Value] =
        Right(Value.JNumber(Left(v)))

      override def visitDouble[E: Error](v: Double): Either[E, Value] =
        Right(Value.JNumber(Right(v)))

      override def visitString[E: Error](v: String): Either[E, Value] =
        Right(Value.JString(v))

      override def visitNone[E: Error](): Either[E, Value] =
        Right(Value.JNull)

      override def visitUnit[E: Error](): Either[E, Value] =
        Right(Value.JNull)

      override def visitSeq[A <: SeqAccess](a: A)(implicit E: Error[a.Error]): Either[a.Error, Value] = {
        val builder = imm.Seq.newBuilder[Value]
        while (true) {
          a.nextElement[Value]()(deserializeValue) match {
            case Right(Some(e)) => builder += e
            case Right(None)    => return Right(Value.JArray(builder.result()))
            case Left(e)        => return Left(e)
          }
        }
        throw new AssertionError("unreachable")
      }

      override def visitMap[A <: MapAccess](a: A)(implicit E: Error[a.Error]): Either[a.Error, Value] = {
        val builder = imm.Map.newBuilder[String, Value]
        while (true) {
          a.nextEntry[String, Value]()(implicitly[Deserialize[String]], deserializeValue) match {
            case Right(Some(e)) => builder += e
            case Right(None)    => return Right(Value.JObject(builder.result()))
            case Left(e)        => return Left(e)
          }
        }
        throw new AssertionError("unreachable")
      }
    }

    override def deserialize[D <: Deserializer](de: D): Either[de.Error, Value] =
      de.deserializeAny(valueVisitor)
  }

}
