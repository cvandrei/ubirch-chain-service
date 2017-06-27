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
  Resolver.bintrayRepo("rick-beton", "maven"), // needed for the notary-client
  Resolver.bintrayRepo("hseeberger", "maven")
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
  Resolver.sonatypeRepo("snapshots"),
  Resolver.bintrayRepo("hseeberger", "maven")
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

### `test-tools`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.bintrayRepo("hseeberger", "maven")
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "test-tools" % "0.1.0-SNAPSHOT"
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

### Transaction

Writes a message or hash to the blockchain.

To write a message to the blockchain (where _msg_ is any valid JSON):

    curl -i -XPOST localhost:8080/api/v1/chainService/tx -H "Content-Type: application/json" -d '{"msg": {"foo": "bar"}}'

To write a hash to the blockchain:

    curl -i -XPOST localhost:8080/api/v1/chainService/tx -H "Content-Type: application/json" -d '{"hash": "e9758380e3f9d2d0b9e0b13e424fcbf94a576c59dcf136b201832d1a687efc86"}'


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

1. Install Dependencies

  * Ubuntu 16.04
    ```
    sudo apt install python3-boto3 python3-pip
    sudo -H pip3 install -r bigChainDbStore/requirements.txt
    ```

  * MacOS
    ```
    tbd
    ```

2. Start BigchainDb

    ```
    python3 bigChainDbStore/src/bigChainDbStore.py
    ```

3. Start Server

    ```
    export AWS_ACCESS_KEY_ID={YOUR AWS ACCESS KEY}
    export AWS_SECRET_ACCESS_KEY={YOUR AWS SECRET KEY}
    ./sbt server/run
    ```


## Create Docker Image

    ./goBuild.sh assembly
