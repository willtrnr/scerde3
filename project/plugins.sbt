val TlVersion = "0.7.1"

addSbtPlugin("org.typelevel" % "sbt-typelevel-github" % TlVersion)
addSbtPlugin("org.typelevel" % "sbt-typelevel-mima" % TlVersion)
addSbtPlugin("org.typelevel" % "sbt-typelevel-scalafix" % TlVersion)
addSbtPlugin("org.typelevel" % "sbt-typelevel-settings" % TlVersion)
addSbtPlugin("org.typelevel" % "sbt-typelevel-site" % TlVersion)
addSbtPlugin("org.typelevel" % "sbt-typelevel-versioning" % TlVersion)

addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.3.2")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.16.0")
addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.5.2")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")
