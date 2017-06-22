packagedArtifacts in file(".") := Map.empty // disable publishing of root/default project

//lazy val testConfiguration = "-Dconfig.resource=" + Option(System.getProperty("test.config")).getOrElse("application.base.conf")

// see http://www.scala-sbt.org/0.13/docs/Parallel-Execution.html for details
concurrentRestrictions in Global := Seq(
  Tags.limit(Tags.Test, 1)
)

lazy val commonSettings = Seq(

  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-feature"),
  organization := "com.ubirch.chain",

  homepage := Some(url("http://ubirch.com")),
  scmInfo := Some(ScmInfo(
    url("https://github.com/ubirch/ubirch-chain-service"),
    "scm:git:git@github.com:ubirch/ubirch-chain-service.git"
  )),
  version := "0.0.1-SNAPSHOT",
  test in assembly := {},
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    resolverSeebergerJson
  )

  //javaOptions in Test += testConfiguration

)

/*
 * MODULES
 ********************************************************/

lazy val chainService = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(server, core, config, testUtil, testBase, share)

lazy val server = project
  .settings(commonSettings: _*)
  .settings(mergeStrategy: _*)
  .dependsOn(share, core, testBase, config)
  .settings(
    libraryDependencies ++= depServer,
    fork in run := true,
    mainClass in(Compile, run) := Some("com.ubirch.chain.backend.Boot")
  )

lazy val core = project
  .settings(commonSettings: _*)
  .dependsOn(testBase, share, config, testUtil)
  .settings(
    libraryDependencies ++= depCore,
    resolvers ++= Seq(
      resolverSeebergerJson,
      resolverHasher,
      resolverBeeClient
    )
  )

lazy val config = project
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies += ubirchUtilConfig
  )

lazy val testUtil = (project in file("test-util"))
  .settings(commonSettings: _*)
  .dependsOn(share)
  .settings(
    name := "test-util",
    resolvers ++= Seq(
      resolverHasher
    ),
    libraryDependencies ++= Seq(
      ubirchUtilDate
    )
  )

lazy val testBase = (project in file("test-base"))
  .settings(commonSettings: _*)
  .settings(
    name := "test-base",
    libraryDependencies ++= depTestBase,
    resolvers ++= Seq(
      resolverBeeClient
    )
  )

lazy val share = project
  .settings(commonSettings: _*)
  .dependsOn(testBase, config)
  .settings(
    libraryDependencies ++= depShare,
    resolvers ++= Seq(
      resolverHasher,
      resolverBeeClient
    )
  )

/*
 * MODULE DEPENDENCIES
 ********************************************************/

lazy val depServer = Seq(

  //akka
  akkaSlf4j,
  akkaHttp,
  ubirchRestAkkaHttp,
  ubirchRestAkkaHttpTest % "test",

  //testing
  scalatest % "test",

  // logging
  typesafeScalaLogging,
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "ch.qos.logback" % "logback-core" % "1.1.3",
  "org.slf4j" % "slf4j-api" % "1.7.12"

  //ubirchUtilJsonAutoConvert

)

lazy val depCore = Seq(
  typesafeScalaLogging,
  ubirchUtilCrypto,
  ubirchStorageModel,
  ubirchNotaryClient,
  ubirchStorageClient,
  scalatest % "test",
  ubirchStorageTestUtil % "test"
)

lazy val depTestBase = Seq(
  scalatest,
  akkaHttpTestkit,
  ubirchStorageTestUtil//,
  //ubirchUtilJsonAutoConvert
)

lazy val depShare = Seq(
  typesafeScalaLogging,
  ubirchUtilCrypto,
  ubirchUtilDate,
  ubirchStorageClient
) ++ json4s

/*
 * DEPENDENCIES
 ********************************************************/

// VERSIONS
val akkaV = "2.4.18"
val akkaHttpV = "10.0.6"
val scalaTestV = "3.0.1"
val json4sV = "3.5.2"
val configV = "1.3.0"
val notaryServiceV = "0.2.7"
val storageServiceV = "0.0.1"

// GROUP NAMES
val ubirchUtilG = "com.ubirch.util"
val json4sG = "org.json4s"
val akkaG = "com.typesafe.akka"

lazy val scalaLogging = Seq(
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2" exclude("org.slf4j", "slf4j-api"),
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0" exclude("org.slf4j", "slf4j-api"),
  "ch.qos.logback" % "logback-core" % "1.1.7",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.internetitem" % "logback-elasticsearch-appender" % "1.4"
)

lazy val akkaActor = akkaG %% "akka-actor" % akkaV
lazy val akkaHttp = akkaG %% "akka-http" % akkaHttpV
lazy val akkaSlf4j = akkaG %% "akka-slf4j" % akkaV

lazy val excludedLoggers = Seq(
  ExclusionRule(organization = "com.typesafe.scala-logging"),
  ExclusionRule(organization = "org.slf4j"),
  ExclusionRule(organization = "ch.qos.logback")
)

lazy val json4s = Seq(
  json4sCore,
  json4sJackson,
  json4sExt,
  seebergerJson4s
)
lazy val json4sJackson = json4sG %% "json4s-jackson" % json4sV
lazy val json4sCore = json4sG %% "json4s-core" % json4sV
lazy val json4sExt = json4sG %% "json4s-ext" % json4sV
lazy val seebergerJson4s = "de.heikoseeberger" %% "akka-http-json4s" % "1.14.0"

lazy val scalatest = "org.scalatest" %% "scalatest" % scalaTestV
lazy val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV

lazy val typesafeScalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"

//lazy val ubirchUtilJsonAutoConvert = ubirchUtilG %% "json-auto-convert" % ubirchUtilJsonAutoConvertV excludeAll(excludedLoggers: _*)
lazy val ubirchUtilConfig = ubirchUtilG %% "config" % "0.1" excludeAll(excludedLoggers: _*)
lazy val ubirchUtilCrypto = ubirchUtilG %% "crypto" % "0.3.4" excludeAll(excludedLoggers: _*)
lazy val ubirchUtilDate = ubirchUtilG %% "date" % "0.1" excludeAll(excludedLoggers: _*)
lazy val ubirchRestAkkaHttp = ubirchUtilG %% "rest-akka-http" % "0.3.7" excludeAll(excludedLoggers: _*)
lazy val ubirchRestAkkaHttpTest = ubirchUtilG %% "rest-akka-http-test" % "0.3.7" excludeAll(excludedLoggers: _*)

lazy val ubirchNotaryClient = "com.ubirch.notary" %% "client" % notaryServiceV excludeAll(excludedLoggers: _*)
lazy val ubirchStorageClient = "com.ubirch.storage" %% "client" % storageServiceV excludeAll(excludedLoggers: _*)
lazy val ubirchStorageTestUtil = "com.ubirch.storage" %% "test-util" % storageServiceV excludeAll(excludedLoggers: _*)
lazy val ubirchStorageModel = "com.ubirch.storage" %% "model" % storageServiceV excludeAll(excludedLoggers: _*)

/*
 * RESOLVER
 ********************************************************/

lazy val resolverSeebergerJson = Resolver.bintrayRepo("hseeberger", "maven")
lazy val resolverHasher = "RoundEights" at "http://maven.spikemark.net/roundeights"
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
    case "reference.conf" => MergeStrategy.concat
    case _ => MergeStrategy.first
  }
)
