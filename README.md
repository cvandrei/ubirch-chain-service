# ubirch Chain Server

## Scala Dependencies

    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots")
    )
    libraryDependencies ++= Seq(
      "com.ubirch.chain" %% "config" % "0.1-SNAPSHOT",
      "com.ubirch.chain" %% "model" % "0.1-SNAPSHOT",
      "com.ubirch.chain" %% "server" % "0.1-SNAPSHOT",
      "com.ubirch.chain" %% "share" % "0.1-SNAPSHOT",
      "com.ubirch.chain" %% "test-base" % "0.1-SNAPSHOT",
      "com.ubirch.chain" %% "test-util" % "0.1-SNAPSHOT
    )

## REST Methods

### Welcome / Health

    curl localhost:8080/

### Hash

    curl -XPOST localhost:8080/api/v1/chainService/hash -H "Content-Type: application/json" -d '{"data": "ubirch-chain-test-2342"}'

### Chain Explorer

Query a block's info based on an event hash:

    curl localhost:8080/api/v1/chainService/explorer/eventHash/e9758380e3f9d2d0b9e0b13e424fcbf94a576c59dcf136b201832d1a687efc86

Query a block's info based on a block hash:

    curl localhost:8080/api/v1/chainService/explorer/blockInfo/e9758380e3f9d2d0b9e0b13e424fcbf94a576c59dcf136b201832d1a687efc86

Query a full block based on a block hash:

    curl localhost:8080/api/v1/chainService/explorer/fullBlock/e9758380e3f9d2d0b9e0b13e424fcbf94a576c59dcf136b201832d1a687efc86

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
