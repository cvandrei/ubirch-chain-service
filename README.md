# ubirch Chain Server

## General Information

TODO

## Scala Dependencies

### `config`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.chain" %% "config" % "0.2-SNAPSHOT"
    )

### `core`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      "RoundEights" at "http://maven.spikemark.net/roundeights", // Hasher
      Resolver.bintrayRepo("rick-beton", "maven") // BeeClient
    )
    libraryDependencies ++= Seq(
      "com.ubirch.chain" %% "core" % "0.2-SNAPSHOT"
    )
        
### `server`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.chain" %% "server" % "0.2-SNAPSHOT"
    )

### `share`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      "RoundEights" at "http://maven.spikemark.net/roundeights", // Hasher
      Resolver.bintrayRepo("rick-beton", "maven") // BeeClient
    )
    libraryDependencies ++= Seq(
      "com.ubirch.chain" %% "share" % "0.2-SNAPSHOT"
    )

### `test-base`

    resolvers ++= Seq(
      Resolver.bintrayRepo("rick-beton", "maven") // BeeClient
    )
    libraryDependencies ++= Seq(
      "com.ubirch.chain" %% "test-base" % "0.2-SNAPSHOT"
    )

### `test-util`

    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      "RoundEights" at "http://maven.spikemark.net/roundeights" // Hasher
    )
    libraryDependencies ++= Seq(
      "com.ubirch.chain" %% "test-util" % "0.2-SNAPSHOT"
    )

## Release Notes

### 0.0.2 (tbd)

* fix broken dependency: com.ubirch.~~backend.~~storage

## REST Methods

### Welcome / Health

    curl localhost:8080/

### Hash

    curl -i -XPOST localhost:8080/api/v1/chainService/hash -H "Content-Type: application/json" -d '{"data": "ubirch-chain-test-2342"}'

### Chain Explorer

Query a block's info based on an event hash:

    curl -i localhost:8080/api/v1/chainService/explorer/eventHash/e9758380e3f9d2d0b9e0b13e424fcbf94a576c59dcf136b201832d1a687efc86

Query a block's info based on a block hash:

    curl -i localhost:8080/api/v1/chainService/explorer/blockInfo/e9758380e3f9d2d0b9e0b13e424fcbf94a576c59dcf136b201832d1a687efc86

Query a the next block info to a given blockHash:

    curl -i localhost:8080/api/v1/chainService/explorer/nextBlockInfo/e9758380e3f9d2d0b9e0b13e424fcbf94a576c59dcf136b201832d1a687efc86

Query a full block based on a block hash:

    curl -i localhost:8080/api/v1/chainService/explorer/fullBlock/e9758380e3f9d2d0b9e0b13e424fcbf94a576c59dcf136b201832d1a687efc86

## Configuration

### akka.http

Configures the Akka-Http Server.

### ubirchChainService

    ubirchChainService {
      interface
      port
      block {
        maxBlockSize
        sizeCheckInterval
        blockInterval
      }
      anchoring {
        enabled
        url
      }
    }
| Key                     | Description |
| ----------------------- | ----------- |
| interface               | network interface the service is connected to |
| port                    | port on which service listens |
| block.checkInterval     | how many seconds between checks if we may mine the next block |
| block.maxSize           | maximum block size in kilobyte |
| block.mineEveryXSeconds | number of seconds after which we mine a new block independent of it's size |
| anchor.enabled          | true if blocks may be anchored into another blockchain |
| anchor.url              | url of Notary Service to send notifications to when anchroing is enabled |
| anchor.interval         | interval (in seconds) in which blocks are anchored |

## Automated Tests

Some of the existing automated tests depend on an ElasticSearch instance running on localhost.

## Local Setup

The service comes with a configuration tailored to running everything on `localhost`.
 
First download, install and start ElasticSearch (version 2.3.3 works).

Open a terminal and run the following command to run the Chain Service:

    sbt server/run

To get random data into the server (resulting in unmined hashes and blocks that you can query) a shell script is
provided in the root folder of this project. It will run until you manually stop it. You can run as many parallel
instances of it as needed to create even more data. Open a terminal to run the script:

    ./hash_random_input.sh
