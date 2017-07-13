# ubirch-chain-service

## General Information

TODO


## Release History

### Version 0.1.1 (2017-07-12)

* update logging dependencies
* update logback configs
* delete unused config key
* update application.docker.conf
* update _com.ubirch.util:mongo(-test)-utils_ to 0.3.3

### Version 0.1.0 (2017-06-30)

* initial release


## Scala Dependencies

### `cmdtools`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases")
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "cmdtools" % "0.1.1"
)
```

### `config`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases")
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "config" % "0.1.1"
)
```

### `core`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.bintrayRepo("rick-beton", "maven"), // needed for the notary-client
  Resolver.bintrayRepo("hseeberger", "maven")
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "core" % "0.1.1"
)
```

### `model-db`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases")
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "model-db" % "0.1.1"
)
```

### `model-rest`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.bintrayRepo("hseeberger", "maven")
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "model-rest" % "0.1.1"
)
```

### `server`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.bintrayRepo("hseeberger", "maven")
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "server" % "0.1.1"
)
```

### `test-tools`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.bintrayRepo("hseeberger", "maven")
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "test-tools" % "0.1.1"
)
```

### `util`

```scala
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases")
)
libraryDependencies ++= Seq(
  "com.ubirch.chain" %% "util" % "0.1.1"
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

#### DeviceData

Writes a device message to the blockchain (where _payload_ is any valid JSON and _id_ is the external id):

    curl -i -XPOST localhost:8080/api/v1/chainService/tx/deviceMsg -H "Content-Type: application/json" -d '{"id": "92b72011-c458-447f-90c5-69ea26e22cf8", "payload": {"t": 29.067}}'

#### DeviceDataHash

Writes a hash to the blockchain (where _hash_ is any String and _id_ is the external id):

    curl -i -XPOST localhost:8080/api/v1/chainService/tx/deviceMsgHash -H "Content-Type: application/json" -d '{"id": "35b3f02d-c9c4-4139-8fe6-6857daeb8b8b", hash": "e9758380e3f9d2d0b9e0b13e424fcbf94a576c59dcf136b201832d1a687efc86"}'


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

1. Installation
 
  1.1. Dependencies

    * Ubuntu 16.04

      ```
      sudo apt install python3-pip
      sudo -H pip3 install -r bigChainDbStore/requirements.txt
      ```

    * MacOS

      ```
      tbd
      ```

  1.2. Bigchain
  
    [BigchainDb Quickstart](https://docs.bigchaindb.com/projects/server/en/latest/quickstart.html)

2. Start Environment

  2.1. BigchainDb
  
    Follow the instruction in: [BigchainDb Quickstart](https://docs.bigchaindb.com/projects/server/en/latest/quickstart.html).

  2.2. Python Wrapper around BigchainDb

    This Python wrapper listens on a queue for new transactions to store in BigchainDb.
    
    ```
    export AWS_ACCESS_KEY_ID={YOUR AWS ACCESS KEY}
    export AWS_SECRET_ACCESS_KEY={YOUR AWS SECRET KEY}
    python3 bigChainDbStore/src/bigChainDbStore.py
    ```

  2.3. Start Server

    ```
    export AWS_ACCESS_KEY_ID={YOUR AWS ACCESS KEY}
    export AWS_SECRET_ACCESS_KEY={YOUR AWS SECRET KEY}
    ./sbt server/run
    ```

3. Generate Test Data

  3.1. Directly to BigchainDb
  
    ```
    export AWS_ACCESS_KEY_ID={YOUR AWS ACCESS KEY}
    export AWS_SECRET_ACCESS_KEY={YOUR AWS SECRET KEY}
    python3 bigChainDbStore/src/bigChainDbTester.py
    ```

  3.2. Device Data Through chain-service
  
    ```
    export AWS_ACCESS_KEY_ID={YOUR AWS ACCESS KEY}
    export AWS_SECRET_ACCESS_KEY={YOUR AWS SECRET KEY}
    python3 bigChainDbStore/src/chainServiceDeviceMsgTester.py
    ```


## Create Docker Image

    ./goBuild.sh assembly
