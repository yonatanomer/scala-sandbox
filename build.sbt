scalaVersion := "2.13.5"
ThisBuild / organization := "com.innerproduct"
ThisBuild / version := "0.0.1-SNAPSHOT"
ThisBuild / fork := true

val commonSettings = Seq(
  // remove fatal warnings since it is handy to have unused and dead code blocks
  scalacOptions --= Seq("-Xfatal-warnings")
)

lazy val kafka_sandbox = (project in file("kafka_sandbox"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Dependencies.Kafka ++ Dependencies.Common
  )

lazy val demo_projects = (project in file("case-studies") / "demo")
  .dependsOn(kafka_sandbox % "test->test;compile->compile")
  .settings(commonSettings)
  .settings(
    scalacOptions += "-Ymacro-annotations", // required by cats-tagless-macros
    libraryDependencies ++= Dependencies.Http ++ Dependencies.Common
  )
