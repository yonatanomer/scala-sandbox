scalaVersion := "2.13.5"

ThisBuild / organization := "com.innerproduct"
ThisBuild / version := "0.0.1-SNAPSHOT"
ThisBuild / fork := true

///mainClass in (Compile, run) := Some("com.yon.kafka_test.ProducerApp")

val commonSettings = Seq(
  // remove fatal warnings since it is handy to have unused and dead code blocks
  scalacOptions --= Seq("-Xfatal-warnings")
)

val commonDeps =
  Dependencies.Http ++
    Dependencies.Common ++
    Dependencies.Kafka ++
    Dependencies.Mongo ++
    Dependencies.Testing

def caseStudy(proj: Project): Project = proj
  .dependsOn(common % "test->test;compile->compile")
  .settings(commonSettings)
  .settings(
    //scalacOptions += "-Ymacro-annotations", // required by cats-tagless-macros
    libraryDependencies ++= commonDeps
  )

lazy val crawler = caseStudy(project in file("case-studies") / "crawler")
lazy val kafka_demo = caseStudy(project in file("case-studies") / "kafka_demo")

lazy val common = (project in file("common"))
  .settings(commonSettings)
  .settings(libraryDependencies ++= commonDeps)
