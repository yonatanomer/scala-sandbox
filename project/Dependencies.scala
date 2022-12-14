import sbt._

object Dependencies {

  val ConfigVersion = "4.6.1"
  val TapirVersion = "1.0.0"
  val http4sVersion = "0.23.12"
  val kafkaVersion = "2.8.0"
  val kafkaSerializationV = "0.5.22"

  val Http: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core" % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % TapirVersion,
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion
  )

  val Mongo: Seq[ModuleID] = Seq(
    "org.mongodb.scala" %% "mongo-scala-driver" % "4.6.0",
    "com.dimafeng" %% "testcontainers-scala-mongodb" % "0.40.10" % Test
  )

  val Kafka: Seq[ModuleID] = Seq(
    "org.apache.kafka" % "kafka-clients" % kafkaVersion,
    "org.apache.kafka" % "kafka-streams" % kafkaVersion,
    "org.apache.kafka" %% "kafka-streams-scala" % kafkaVersion
  )

  val Testing: Seq[ModuleID] = Seq(
    "org.specs2" %% "specs2-junit" % "4.16.0",
    "org.scalatest" %% "scalatest" % "3.2.13",
    "org.scalatest" %% "scalatest-flatspec" % "3.2.12",
    "org.mock-server" % "mockserver-client-java" % "5.14.0",
    "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.40.10",
    "com.dimafeng" %% "testcontainers-scala-mockserver" % "0.40.10"
    //    Spec2 %% "specs2-core" % Versions.Spec2,
    //    Mockito % "mockito-core" % Versions.Mockito,
    //    Spec2 %% "specs2-mock" % Versions.Spec2,
    //    Tapir %% "tapir-sttp-stub-server" % Versions.Tapir
  ).map(_ % Test)

//  val IntegrationTesting: Seq[ModuleID] = Seq(
//    TestcontainersScala %% "testcontainers-scala-scalatest" % Versions.TestcontainersScala,
//    TestcontainersScala %% "testcontainers-scala-mockserver" % Versions.TestcontainersScala,
//    TestcontainersScala %% "testcontainers-scala-mongodb" % Versions.TestcontainersScala,
//    Spec2 %% "specs2-core" % Versions.Spec2,
//    ScalaTest %% "scalatest" % Versions.ScalaTest
//  ).map(_ % "test, it")

  // these are transitive dependencies of the above, but we might want to be explicit about them
  val CatsVersion = "3.3.12"
  val circeVersion = "0.14.1"
  val fs2Version = "3.2.7"

  val Common: Seq[ModuleID] = Seq(
    //"com.typesafe" % "config" % "1.4.2"
    "com.github.pureconfig" %% "pureconfig" % "0.17.2"

    //"org.typelevel" %% "cats-core" % "2.5.0",
    //"org.typelevel" %% "cats-effect" % "3.3.12",
    //"io.circe" %% "circe-core" % circeVersion,
    //"io.circe" %% "circe-generic" % circeVersion,
    //"io.circe" %% "circe-parser" % circeVersion,
    //"co.fs2" %% "fs2-core" % fs2Version,
    //"co.fs2" %% "fs2-io" % fs2Version,
    //"co.fs2" %% "fs2-reactive-streams" % fs2Version
  )

}
