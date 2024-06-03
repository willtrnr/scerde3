package scerde
package de

trait Deserializer {
  type Error

  def deserializeAny[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeBool[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeByte[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeShort[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeInt[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeLong[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeUByte[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeUShort[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeUInt[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeULong[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeFloat[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeDouble[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeChar[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeString[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeBytes[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeOption[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeUnit[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeUnitStruct[V <: Visitor](name: String, visitor: V): Either[Error, visitor.Value]

  def deserializeNewtypeStruct[V <: Visitor](name: String, visitor: V): Either[Error, visitor.Value]

  def deserializeSeq[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeTuple[V <: Visitor](len: Int, visitor: V): Either[Error, visitor.Value]

  def deserializeTupleStruct[V <: Visitor](name: String, len: Int, visitor: V): Either[Error, visitor.Value]

  def deserializeMap[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeStruct[V <: Visitor](name: String, fields: Seq[String], visitor: V): Either[Error, visitor.Value]

  def deserializeEnum[V <: Visitor](name: String, variants: Seq[String], visitor: V): Either[Error, visitor.Value]

  def deserializeIdentifier[V <: Visitor](visitor: V): Either[Error, visitor.Value]

  def deserializeIgnoredAny[V <: Visitor](visitor: V): Either[Error, visitor.Value]
}
