# ubirch Chain Server

## REST Methods

### Welcome / Health

    curl localhost:8080/

### Hash

    curl -XPOST localhost:8080/api/v1/chainService/hash -H "Content-Type: application/json" -d '{"data": "ubirch-chain-test-2342"}'

### Chain Explorer

    curl localhost:8080/api/v1/chainService/explorer/hash/e9758380e3f9d2d0b9e0b13e424fcbf94a576c59dcf136b201832d1a687efc86
    
    curl localhost:8080/api/v1/chainService/explorer/block/e9758380e3f9d2d0b9e0b13e424fcbf94a576c59dcf136b201832d1a687efc86

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
| block.maxSize           | maximum block size in kilobyte |
| block.sizeCheckInterval | milliseconds between checks if the maxBlockSize has been reached |
| block.interval          | number of seconds after which a block will definitely be generated |
| anchor.enabled          | true if blocks may be anchored into another blockchain |
| anchor.url              | url of Notary Service to send notifications to when anchroing is enabled |
