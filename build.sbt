import sbtcrossproject.CrossProject

import scerde.sbt.ScerdeBuild._

ThisBuild / organization := "net.archwill.scerde"
ThisBuild / organizationName := "Archwill"
ThisBuild / startYear := Some(2024)
ThisBuild / licenses := Seq(License.MIT)
ThisBuild / developers := List(
  tlGitHubDev("willtrnr", "William Turner")
)

ThisBuild / tlBaseVersion := "0.1"

ThisBuild / crossScalaVersions := Seq(Scala3, Scala213, Scala212)
ThisBuild / scalaVersion := Scala213
ThisBuild / scalacOptions ++= {
  if (tlIsScala3.value)
    Seq("-old-syntax", "-noindent")
  else
    Seq.empty
}


lazy val core =
  scerdeCrossModule("core")
    .settings(
      libraryDependencies ++= Seq(
        munit.value % Test
      )
    )

lazy val derive =
  scerdeCrossModule("derive")
    .settings(
      libraryDependencies ++= Seq(
        munit.value % Test
      )
    )
    .dependsOn(
      core
    )

lazy val cats =
  scerdeCrossModule("cats")
    .settings(
      libraryDependencies ++= Seq(
        munit.value % Test
      )
    )
    .dependsOn(
      core
    )

lazy val json =
  scerdeCrossModule("json")
    .settings(
      libraryDependencies ++= Seq(
        munit.value % Test
      )
    )
    .dependsOn(
      core
    )

lazy val jsonJackson2 =
  scerdeModule("json-jackson2")
    .settings(
      libraryDependencies ++= Seq(
        jackson2Core.value,
        munit.value % Test,
      )
    )
    .dependsOn(
      core.jvm,
      json.jvm,
    )

def scerdeProject(n: String)(project: Project) =
  project.settings(
    name := s"Scerde $n",
    moduleName := s"scerde-$n",
    description := s"scerde $n",
  )

def scerdeModule(n: String) =
  Project(n, file(s"modules/$n"))
    .configure(scerdeProject(n))

def scerdeCrossModule(n: String) =
  CrossProject(n, file(s"modules/$n"))(JSPlatform, JVMPlatform, NativePlatform)
    .configure(scerdeProject(n))
