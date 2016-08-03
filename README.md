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
      hash {
        algorithm
      }
      block {
        maxBlockSize
        sizeCheckInterval
        blockInterval
      }
    }
| Key                     | Description |
| ----------------------- | ----------- |
| interface               | network interface the service is connected to |
| port                    | port on which service listens |
| hash.algorithm          | algorithm used to calculate hashes on data inputs or when calculating blocks. |
| block.maxSize           | maximum block size in kilobyte |
| block.sizeCheckInterval | milliseconds between checks if the maxBlockSize has been reached |
| block.interval          | number of seconds after which a block will definitely be generated |

### Valid Values: `hash.algorithm`

Value will be converted to lowercase internally so you can use upper and lower cases as you like.

  * MD5
  * SHA1
  * SHA256
  * SHA384
  * SHA512
  * BCrypt
  * CRC32
