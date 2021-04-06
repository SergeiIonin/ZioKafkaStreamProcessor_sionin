import sbt.Keys.scalaVersion

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.13.5"
    )),
    name := "ZIOStreamProcessor",
    libraryDependencies ++= zioDeps ++ zioLoggingDeps ++ circeDeps ++ loggingDeps ++ testDependencies,
    cancelable := false
  )

val zio_version        = "1.0.3"
val zioKafka_version   = "0.13.0"
val zioLogging_version = "0.4.0"
val circe_version      = "0.13.0"
val sttp_version       = "2.2.9"
val log4j_version      = "2.13.3"
val disruptor_version  = "3.4.2"

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

