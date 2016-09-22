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
    url("https://gitlab.com/ubirch/ubirchChainService"),
    "scm:git:https://gitlab.com/ubirch/ubirchChainService.git"
  )),
  version := "0.2-SNAPSHOT",
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
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-http-experimental" % akkaV,

  //testing
  scalatest % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
  akkaHttpTestkit % "test",

  // logging
  typesafeScalaLogging,
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "ch.qos.logback" % "logback-core" % "1.1.3",
  "org.slf4j" % "slf4j-api" % "1.7.12",

  ubirchUtilJsonAutoConvert,
  ubirchUtilRestAkkaHttp

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
  ubirchStorageTestUtil,
  ubirchUtilJsonAutoConvert
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

val akkaV = "2.4.9-RC2"
val scalaTestV = "3.0.0"
val json4sV = "3.4.0"
val configV = "1.3.0"
val notaryServiceV = "0.3.0-SNAPSHOT"
val storageServiceV = "0.0.1-SNAPSHOT"
val ubirchUtilCryptoV = "0.2-SNAPSHOT"
val ubirchUtilJsonAutoConvertV = "0.1-SNAPSHOT"

lazy val json4s = Seq(
  json4sCore,
  json4sJackson,
  json4sExt,
  seebergerJson4s
)
lazy val json4sJackson = "org.json4s" %% "json4s-jackson" % json4sV
lazy val json4sCore = "org.json4s" %% "json4s-core" % json4sV
lazy val json4sExt = "org.json4s" %% "json4s-ext" % json4sV
lazy val seebergerJson4s = "de.heikoseeberger" %% "akka-http-json4s" % "1.8.0"

lazy val scalatest = "org.scalatest" %% "scalatest" % scalaTestV
lazy val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaV

lazy val typesafeScalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"

lazy val ubirchUtilConfig = "com.ubirch.util" %% "config" % "0.1-SNAPSHOT"
lazy val ubirchUtilDate = "com.ubirch.util" %% "date" % "0.1-SNAPSHOT"
lazy val ubirchUtilRestAkkaHttp = "com.ubirch.util" %% "rest-akka-http" % "0.1"
lazy val ubirchNotaryClient = "com.ubirch.notary" %% "client" % notaryServiceV
lazy val ubirchStorageClient = "com.ubirch.backend.storage" %% "client" % storageServiceV
lazy val ubirchStorageTestUtil = "com.ubirch.backend.storage" %% "test-util" % storageServiceV
lazy val ubirchStorageModel = "com.ubirch.backend.storage" %% "model" % storageServiceV
lazy val ubirchUtilCrypto = "com.ubirch.util" %% "crypto" % ubirchUtilCryptoV
lazy val ubirchUtilJsonAutoConvert = "com.ubirch.util" %% "json-auto-convert" % ubirchUtilJsonAutoConvertV

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
