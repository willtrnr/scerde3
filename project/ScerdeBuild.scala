package scerde.sbt

import sbt._

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object ScerdeBuild {

  val Scala212 = "2.12.19"
  val Scala213 = "2.13.14"
  val Scala3 = "3.3.3"

  object V {
    val jackson2 = "2.12.1"
    val munit = "1.0.0"
  }

  lazy val jackson2Core = Def.setting("com.fasterxml.jackson.core" % "jackson-core" % V.jackson2)

  lazy val munit = Def.setting("org.scalameta" %%% "munit" % V.munit)

}
