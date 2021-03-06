parallelExecution in Test := false // test related: http://www.scalatest.org/user_guide/using_scalatest_with_sbt
// see http://www.scala-sbt.org/0.13/docs/Parallel-Execution.html for details
concurrentRestrictions in Global := Seq(
  Tags.limit(Tags.Test, 1)
)

val commonSettings = Seq(

  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-feature"),
  organization := "com.ubirch.chain",

  homepage := Some(url("http://ubirch.com")),
  scmInfo := Some(ScmInfo(
    url("https://github.com/ubirch/ubirch-chain-service"),
    "scm:git:git@github.com:ubirch/ubirch-chain-service.git"
  )),
  version := "0.3.0",
  test in assembly := {},
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases")
  )

)

/*
 * MODULES
 ********************************************************/

lazy val chainService = (project in file("."))
  .settings(
    commonSettings,
    skip in publish := true
  )
  .aggregate(
    cmdtools,
    config,
    core,
    modelDb,
    modelRest,
    server,
    testTools,
    util
  )

lazy val cmdtools = project
  .settings(commonSettings)
  .dependsOn(config, testTools)
  .settings(
    description := "command line tools"
  )

lazy val config = project
  .settings(commonSettings)
  .settings(
    description := "chain-service specific config and config tools",
    libraryDependencies += ubirchConfig
  )

lazy val core = project
  .settings(commonSettings)
  .dependsOn(config, modelDb, modelRest, util, testTools % "test")
  .settings(
    description := "business logic",
    libraryDependencies ++= depCore,
    resolvers ++= Seq(
      resolverSeebergerJson,
      resolverBeeClient
    )
  )

lazy val modelDb = (project in file("model-db"))
  .settings(commonSettings)
  .settings(
    name := "model-db",
    description := "Database models",
    libraryDependencies ++= depModelDb
  )

lazy val modelRest = (project in file("model-rest"))
  .settings(commonSettings)
  .settings(
    name := "model-rest",
    description := "JSON models",
    libraryDependencies ++= depModelRest,
    resolvers ++= Seq(
      resolverSeebergerJson
    )
  )

lazy val server = project
  .settings(commonSettings)
  .settings(mergeStrategy: _*)
  .dependsOn(config, core, modelDb, modelRest, util)
  .enablePlugins(DockerPlugin)
  .settings(
    description := "REST interface and Akka HTTP specific code",
    libraryDependencies ++= depServer,
    fork in run := true,
    resolvers ++= Seq(
      resolverSeebergerJson
    ),
    mainClass in(Compile, run) := Some("com.ubirch.chain.server.Boot"),
    resourceGenerators in Compile += Def.task {
      generateDockerFile(baseDirectory.value / ".." / "Dockerfile.input", (assemblyOutputPath in assembly).value)
    }.taskValue
  )

lazy val testTools = (project in file("test-tools"))
  .settings(commonSettings)
  .dependsOn(config)
  .settings(
    name := "test-tools",
    description := "tools useful in automated tests",
    libraryDependencies ++= depTestTools,
    resolvers ++= Seq(
      resolverSeebergerJson
    )
  )

lazy val util = project
  .settings(commonSettings)
  .settings(
    description := "utils",
    libraryDependencies ++= depUtils
  )

/*
 * MODULE DEPENDENCIES
 ********************************************************/

lazy val depServer = Seq(

  akkaSlf4j,
  akkaHttp,
  akkaStream,
  ubirchRestAkkaHttp,
  ubirchRestAkkaHttpTest % "test",

  ubirchResponse

)

lazy val depCore = Seq(
  akkaActor,
  ubirchDeepCheckModel,
  ubirchMongo,
  ubirchNotaryClient,
  ubirchJson,
  scalatest % "test"
) ++ akkaCamel ++ scalaLogging

lazy val depModelDb = Seq(
  ubirchUuid,
  ubirchDate
)

lazy val depModelRest = Seq(
  ubirchUuid,
  ubirchDate,
  ubirchJson
)

lazy val depTestTools = Seq(
  ubirchJson,
  ubirchMongo,
  ubirchMongoTest,
  scalatest
) ++ scalaLogging

lazy val depUtils = Seq(
)

/*
 * DEPENDENCIES
 ********************************************************/

// VERSIONS
val akkaV = "2.5.11"
val akkaHttpV = "10.1.3"
val camelV = "2.22.0"

val scalaTestV = "3.0.5"

val logbackV = "1.2.3"
val slf4jV = "1.7.25"

// GROUP NAMES
val ubirchUtilG = "com.ubirch.util"
val akkaG = "com.typesafe.akka"
val camelG = "org.apache.camel"

lazy val scalatest = "org.scalatest" %% "scalatest" % scalaTestV

lazy val scalaLogging = Seq(
  "org.slf4j" % "slf4j-api" % slf4jV,
  "org.slf4j" % "log4j-over-slf4j" % slf4jV,
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2" exclude("org.slf4j", "slf4j-api"),
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2" exclude("org.slf4j", "slf4j-api"),
  "ch.qos.logback" % "logback-core" % logbackV exclude("org.slf4j", "slf4j-api"),
  "ch.qos.logback" % "logback-classic" % logbackV exclude("org.slf4j", "slf4j-api"),
  "com.internetitem" % "logback-elasticsearch-appender" % "1.5" exclude("org.slf4j", "slf4j-api")
)

lazy val akkaActor = akkaG %% "akka-actor" % akkaV
lazy val akkaHttp = akkaG %% "akka-http" % akkaHttpV
lazy val akkaSlf4j = akkaG %% "akka-slf4j" % akkaV
val akkaStream = akkaG %% "akka-stream" % akkaV

lazy val akkaCamel = Seq(
  camelG % "camel-core" % camelV,
  camelG % "camel-aws" % camelV,
  camelG % "camel-paho" % camelV,
  camelG % "camel-mqtt" % camelV,
  akkaG %% "akka-camel" % akkaV exclude("org.apache.camel", "camel-core")
)

lazy val excludedLoggers = Seq(
  ExclusionRule(organization = "com.typesafe.scala-logging"),
  ExclusionRule(organization = "org.slf4j"),
  ExclusionRule(organization = "ch.qos.logback")
)

val ubirchConfig = ubirchUtilG %% "config" % "0.2.3" excludeAll(excludedLoggers: _*)
val ubirchDate = ubirchUtilG %% "date" % "0.5.3" excludeAll(excludedLoggers: _*)
val ubirchDeepCheckModel = ubirchUtilG %% "deep-check-model" % "0.3.1" excludeAll(excludedLoggers: _*)
val ubirchJson = ubirchUtilG %% "json" % "0.5.1" excludeAll(excludedLoggers: _*)
val ubirchMongoTest = ubirchUtilG %% "mongo-test-utils" % "0.8.4" excludeAll(excludedLoggers: _*)
val ubirchMongo = ubirchUtilG %% "mongo-utils" % "0.8.4" excludeAll(excludedLoggers: _*)
val ubirchResponse = ubirchUtilG %% "response-util" % "0.5.0" excludeAll(excludedLoggers: _*)
val ubirchRestAkkaHttp = ubirchUtilG %% "rest-akka-http" % "0.4.0" excludeAll(excludedLoggers: _*)
val ubirchRestAkkaHttpTest = ubirchUtilG %% "rest-akka-http-test" % "0.4.0" excludeAll(excludedLoggers: _*)
val ubirchUuid = ubirchUtilG %% "uuid" % "0.1.3" excludeAll(excludedLoggers: _*)

lazy val ubirchNotaryClient = "com.ubirch.notary" %% "client" % "0.2.7" excludeAll(excludedLoggers: _*)

/*
 * RESOLVER
 ********************************************************/

lazy val resolverSeebergerJson = Resolver.bintrayRepo("hseeberger", "maven")
lazy val resolverBeeClient = Resolver.bintrayRepo("rick-beton", "maven")

/*
 * MISC
 ********************************************************/

lazy val mergeStrategy = Seq(
  assemblyMergeStrategy in assembly := {
    case PathList("org", "joda", "time", xs@_*) => MergeStrategy.first
    case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
    case m if m.toLowerCase.matches("meta-inf.*\\.sf$") => MergeStrategy.discard
    case m if m.toLowerCase.endsWith("application.conf") => MergeStrategy.concat
    case m if m.toLowerCase.endsWith("application.dev.conf") => MergeStrategy.first
    case m if m.toLowerCase.endsWith("application.base.conf") => MergeStrategy.first
    case m if m.toLowerCase.endsWith("logback.xml") => MergeStrategy.first
    case m if m.toLowerCase.endsWith("logback-test.xml") => MergeStrategy.discard
    case "reference.conf" => MergeStrategy.concat
    case _ => MergeStrategy.first
  }
)

def generateDockerFile(file: File, jarFile: sbt.File): Seq[File] = {
  val contents =
    s"""SOURCE=server/target/scala-2.11/${jarFile.getName}
       |TARGET=${jarFile.getName}
       |""".stripMargin
  IO.write(file, contents)
  Seq(file)
}
