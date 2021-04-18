import sbt.Def.settings
import sbt.Keys.{libraryDependencies, scalaVersion}

import scala.sys.process.Process

lazy val root = (project in file(".")).
  enablePlugins(DockerPlugin).
  enablePlugins(AshScriptPlugin).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.13.5"
    )),
    name := "ZIOStreamProcessor",
    packageName in Docker := "zio-kafka-stream-processor-9",
    dockerExposedPorts ++= Seq(8085),
    dockerUpdateLatest := true,
    dockerBaseImage := "openjdk:8u201-jre-alpine3.9",
    libraryDependencies ++= zioDeps ++ zioLoggingDeps ++ circeDeps ++ jacksonDeps ++ loggingDeps ++ testDependencies,
    cancelable := false
  )

val zio_version        = "1.0.3"
val zioKafka_version   = "0.13.0"
val zioLogging_version = "0.4.0"
val circe_version      = "0.13.0"
val sttp_version       = "2.2.9"
val log4j_version      = "2.13.3"
val disruptor_version  = "3.4.2"
val jackson_version  = "2.12.0"

val zioDeps = Seq(
  "dev.zio" %% "zio"         % zio_version,
  "dev.zio" %% "zio-streams" % zio_version,
  "dev.zio" %% "zio-kafka"   % zioKafka_version
)

val zioLoggingDeps = Seq(
  "dev.zio" %% "zio-logging"       % zioLogging_version,
  "dev.zio" %% "zio-logging-slf4j" % zioLogging_version
)

val circeDeps = Seq(
  "io.circe" %% "circe-core"    % circe_version,
  "io.circe" %% "circe-generic" % circe_version,
  "io.circe" %% "circe-parser" % circe_version
)

val jacksonDeps = Seq("com.fasterxml.jackson.core" % "jackson-databind" % jackson_version)

val loggingDeps = Seq(
  "org.apache.logging.log4j" % "log4j-core"       % log4j_version,
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4j_version,
  "com.lmax"                 % "disruptor"        % disruptor_version
)

val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.8",
  "dev.zio" %% "zio-test"     % zio_version,
  "dev.zio" %% "zio-test-sbt" % zio_version
) map (_ % Test)

/*val excludeFromJar = "producer"
def toAddToJar(toPath: String) = {
  toPath.split("/").toList.headOption.fold(true)(path => path != excludeFromJar)
}

mappings in (Compile,packageBin) ~= { (ms: Seq[(File, String)]) =>
  ms filter { case (file, toPath) =>
    toAddToJar(toPath)
  }
}

mainClass in (Compile, run) := Some("processing.ProcessingApp")

val sudo = taskKey[Unit]("Executes commands with sudo!")

sudo := {
  Process("sbt docker:publishLocal", new File(".")).!<
}*/
