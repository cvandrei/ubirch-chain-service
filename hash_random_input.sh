#!/usr/bin/env bash

while true; do

    deviceId="${RANDOM}${RANDOM}${RANDOM}${RANDOM}"
    data="${RANDOM}${RANDOM}${RANDOM}${RANDOM}"
    json="{\"deviceId\": $deviceId, \"data\": \"$data\"}"

    curl -XPOST localhost:8080/api/v1/chainService/hash -H "Content-Type: application/json" -d "$json"
    echo "\n"

    sleep 0.05

done
