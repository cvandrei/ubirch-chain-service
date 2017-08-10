#!/usr/bin/python3

import boto3
import logging
import os
import json
import random
import uuid

FORMAT = '%(asctime)-15s [%(levelname)s] %(message)s'
logging.basicConfig(format=FORMAT)
logger = logging.getLogger('bigChainDbTester')
logger.setLevel(logging.DEBUG)

awsAccessKeyId = 'AWS_ACCESS_KEY_ID'
awsSecretAccessKey = 'AWS_SECRET_ACCESS_KEY'
sqsChainInKey = 'SQS_CHAIN_IN'
sqsRegionKey = 'SQS_REGION'
numThreadsKey = 'NUM_TRHEADS'

if (awsAccessKeyId not in os.environ or awsSecretAccessKey not in os.environ):
    logger.error("env vars missing")
    logger.info("AWS_ACCESS_KEY_ID -> AWS access key")
    logger.info("AWS_SECRET_ACCESS_KEY -> AWS secret key")
    logger.info(
        "SQS_CHAIN_IN -> AWS SQS queue name of inbound data which should be stored into bigChainDb (optional), default is 'local_dev_ubirch_bigchaindb_in'")
    logger.info("SQS_REGION -> AWS region (optional), default is 'eu-west-1'")
    logger.info("NUM_TRHEADS -> # of threads which poll new messages (optional), , default is 1")
    exit(1)

REGION = os.environ[sqsRegionKey] if sqsRegionKey in os.environ else 'eu-west-1'

sqs = boto3.resource('sqs', region_name=REGION)

inQueue = os.environ[sqsChainInKey] if sqsChainInKey in os.environ else "local_dev_ubirch_bigchaindb_in"
logger.info("inQueue: %s" % inQueue)

numThreads = os.environ[numThreadsKey] if numThreadsKey in os.environ else 1

def sendPayload(payload):
    sqs = boto3.resource('sqs', region_name=REGION)
    queue = sqs.get_queue_by_name(QueueName=inQueue)
    queue.send_message(MessageBody=payload)


for i in range(5):
    t = round((-10 + (random.randint(1, 50000) / 1000)), 3)
    jsonPayload = {
        "id": str(uuid.uuid4()),
        "payload": {
            "t": t
        }
    }
    payload = json.dumps(jsonPayload)
    logger.info("send payload: %s" % payload)
    sendPayload(payload)
