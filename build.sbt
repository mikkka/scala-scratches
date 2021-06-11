val _organization = "name.mtkachev"

name := "scala_scratches"

val _scalaVersion = "2.12.13"

addCompilerPlugin("org.typelevel" % s"kind-projector_${_scalaVersion}" % "0.11.3")

lazy val commonSettings = Seq(
  organization := _organization,
  scalaVersion := _scalaVersion,
  version := "0.0.1",
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
)

val circeVersion = "0.10.0"
val http4sVersion = "0.19.0-M3"

lazy val macros = (project in file("macros"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies += "org.scala-lang" % "scala-reflect" % _scalaVersion
  )

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "scala-scratches",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.1.1",
      "org.typelevel" %% "cats-effect" % "2.1.2",
      "com.olegpy" %% "meow-mtl" % "0.1.3"
    ),

    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl",
      "org.http4s" %% "http4s-blaze-server",
      "org.http4s" %% "http4s-circe",
    ).map(_ % http4sVersion),

    libraryDependencies ++= Seq(
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-literal"
    ).map(_ % circeVersion),

    libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test",
    libraryDependencies += "org.tpolecat" %% "doobie-core"   % "0.8.2",
    libraryDependencies += "org.tpolecat" %% "doobie-hikari" % "0.8.2",
    libraryDependencies += "com.h2database" % "h2" % "1.4.200",
    libraryDependencies += "ru.tinkoff" %% "tofu-core" % "0.6.0",

    libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.3",
    libraryDependencies += "dev.zio" %% "zio" % "1.0.1",
    libraryDependencies += "dev.zio" %% "zio-interop-cats" % "2.1.4.0",


    scalacOptions ++= Seq(
      "-feature",
      "-unchecked",
      "-language:higherKinds",
      "-language:postfixOps",
      "-Ypartial-unification",
      "-deprecation",
      // "-Ymacro-debug-verbose",
      // "-Xlog-implicits"
    )
  ).dependsOn(macros)