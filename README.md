Scerde
======

This is esentially a transliteration into Scala of the `serde` crate for Rust.

At the moment, this is nothing more than an experiment to see how a generic type-safe
and reflection-less serialization/deserialization framework could look like in Scala.

The idea was fueled by the frustration of having to deal with codec derivation for every
format seperately and the dependency hell or framework lock-in that insues when
trying to use those as libraries.

Why not use...
--------------

- **Akka Serialization**
  - Configuration driven, so essentially undefined runtimate behavior.
  - Depends on dynamic runtime class loading order and such.
  - JVM only.
- **Jackson**
  - While many formats are supported, it's mostly JSON oriented.
  - Binding API is reflection based.
  - JVM only.
