# ubirch Chain Server

## REST Methods

### Welcome / Health

    curl localhost:8080/

### Hash

    curl -XPOST localhost:8080/api/v1/chainService/hash -H "Content-Type: application/json" -d '{"data": "ubirch-chain-test-2342"}'

### Chain Explorer

    curl localhost:8080/api/v1/chainService/explorer/hash/2342-data-hash-2342
    
    curl localhost:8080/api/v1/chainService/explorer/block/2342-block-hash-2342

## Configuration

### akka.http

Configures the Akka-Http Server.

### ubirchChainService

    ubirchChainService {
      interface
      port
      hashAlgorithm
    }
| Key            | Description |
| -------------  | ----------- |
| interface      | network interface the service is connected to |
| port           | port on which service listens |
| hash.algorithm | algorithm used to calculate hashes on data inputs or when calculating blocks. |

### Valid Values: `hash.algorithm`

Value will be converted to lowercase internally so you can use upper and lower cases as you like.

  * MD5
  * SHA1
  * SHA256
  * SHA384
  * SHA512
  * BCrypt
  * CRC32
