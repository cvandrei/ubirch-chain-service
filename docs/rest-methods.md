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

    curl -i -XPOST localhost:8080/api/v1/chainService/tx/deviceMsgHash -H "Content-Type: application/json" -d '{"id": "35b3f02d-c9c4-4139-8fe6-6857daeb8b8b", "hash": "e9758380e3f9d2d0b9e0b13e424fcbf94a576c59dcf136b201832d1a687efc86"}'
