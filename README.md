# ubirch-chain-service

## General Information

TODO


## Release History

### Version 0.1.0 (tbd)

* initial release


## Scala Dependencies

### `cmdtools`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "cmdtools" % "0.1.0-SNAPSHOT"
)
```

### `config`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "config" % "0.1.0-SNAPSHOT"
)
```

### `core`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.bintrayRepo("rick-beton", "maven") // needed for the notary-client
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "core" % "0.1.0-SNAPSHOT"
)
```

### `model-db`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "model-db" % "0.1.0-SNAPSHOT"
)
```

### `model-rest`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "model-rest" % "0.1.0-SNAPSHOT"
)
```

### `server`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.bintrayRepo("hseeberger", "maven")
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "server" % "0.1.0-SNAPSHOT"
)
```

### `util`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "util" % "0.1.0-SNAPSHOT"
)
```


## REST Methods

### Welcome / Health / Check

    curl localhost:8100/
    curl localhost:8100/api/chainService/v1
    curl localhost:8100/api/chainService/v1/check

If healthy the server response is:

    200 {"version":"1.0","status":"OK","message":"Welcome to the ubirchChainService ( $GO_PIPELINE_NAME / $GO_PIPELINE_LABEL / $GO_PIPELINE_REVISION )"}

If not healthy the server response is:

    400 {"version":"1.0","status":"NOK","message":"$ERROR_MESSAGE"}

### Deep Check / Server Health

    curl localhost:8100/api/chainService/v1/deepCheck

If healthy the response is:

    200 {"status":true,"messages":[]}

If not healthy the status is `false` and the `messages` array not empty:

    503 {"status":false,"messages":["unable to connect to the database"]}


## Configuration

TODO


## Deployment Notes

TODO


## Automated Tests

run all tests

    ./sbt test

### generate coverage report

    ./sbt coverageReport

more details here: https://github.com/scoverage/sbt-scoverage


## Local Setup

TODO


## Create Docker Image

    ./goBuild.sh assembly
