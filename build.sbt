import sbt.Keys._

name := "ubirchChainService"

version := "1.0"

scalaVersion := "2.11.8"

val akkaV = "2.4.8"
val scalaTestV = "2.2.6"
val json4sV = "3.4.0"
val configV = "1.3.0"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")
resolvers += Resolver.bintrayRepo("rick-beton", "maven")
resolvers ++= Seq("RoundEights" at "http://maven.spikemark.net/roundeights")

libraryDependencies := Seq(

  //scala
  "org.scala-lang.modules" %% "scala-xml" % "1.0.5",
  "org.scala-lang" % "scala-reflect" % "2.11.8",
  "org.scala-lang" % "scala-library" % "2.11.8",

  //akka
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-http-experimental" % akkaV,

  //testing
  "org.scalatest"     %% "scalatest"         % scalaTestV % "test",
  "com.typesafe.akka" %% "akka-testkit"      % akkaV      % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % akkaV      % "test",

  //json4s
  "org.json4s" %% "json4s-core" % json4sV,
  "org.json4s" %% "json4s-native" % json4sV,
  "org.json4s" %% "json4s-ast" % json4sV,
  "org.json4s" %% "json4s-core" % json4sV,
  "org.json4s" %% "json4s-jackson" % json4sV,
  "org.json4s" %% "json4s-ext" % json4sV,
  "de.heikoseeberger" %% "akka-http-json4s" % "1.8.0",

  // app config
  "com.typesafe" % "config" % configV,

  // logging
  "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "ch.qos.logback" % "logback-core" % "1.1.3",
  "org.slf4j" % "slf4j-api" % "1.7.12",

  // hashing
  "com.roundeights" %% "hasher" % "1.2.0",

  // misc
  "uk.co.bigbeeconsultants" %% "bee-client" % "0.29.1"

)