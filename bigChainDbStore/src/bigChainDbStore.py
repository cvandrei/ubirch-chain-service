#!/usr/bin/python3

# pip install -r requirements.txt

import os
import boto3
import json
import logging

from bigchaindb_driver import BigchainDB
from bigchaindb_driver.crypto import generate_keypair

from cmreslogging.handlers import CMRESHandler

import threading

FORMAT = '%(asctime)-15s [%(levelname)s] %(message)s'
logging.basicConfig(format=FORMAT)
logger = logging.getLogger('bigChainDbStore')
logger.setLevel(logging.DEBUG)

awsAccessKeyId = 'AWS_ACCESS_KEY_ID'
awsSecretAccessKey = 'AWS_SECRET_ACCESS_KEY'
sqsChainInKey = 'SQS_UBIRCH_BIGCHAIN_DB_IN'
sqsChainTxKey = 'SQS_UBIRCH_BIGCHAIN_DB_TX'
sqsRegionKey = 'AWS_REGION'
ipdbAppIdKey = 'IPDB_APP_ID'
ipdbAppKeyKey = 'IPDB_APP_KEY'
bigChainDbHostKey = 'BIG_CHAIN_DB_HOST'
numThreadsKey = 'NUM_TRHEADS'
esLoggerHostKey = 'ES_LOG_HOST'
esLoggerPortKey = 'ES_LOG_PORT'

if (esLoggerHostKey in os.environ.keys()):
    esLoggerHost = os.environ[esLoggerHostKey]
    esLoggerPort = int(os.environ[esLoggerPortKey])
    esLoggerHandler = CMRESHandler(hosts=[{'host': esLoggerHost, 'port': esLoggerPort}],
                                   auth_type=CMRESHandler.AuthType.NO_AUTH,
                                   es_index_name="big-chain-store-service-logs")
    logger.addHandler(esLoggerHandler)

if (awsAccessKeyId not in os.environ or awsSecretAccessKey not in os.environ):
    logger.error("env vars missing")
    logger.info("AWS_ACCESS_KEY_ID -> AWS access key")
    logger.info("AWS_SECRET_ACCESS_KEY -> AWS secret key")
    logger.info(
        "SQS_CHAIN_IN -> AWS SQS queue name of inbound data which should be stored into bigChainDb (optional), default is 'local_dev_ubirch_bigchaindb_in'")
    logger.info(
        "SQS_CHAIN_TX -> AWS SQS queue name for outbound tx hash publishing (optional), default is 'local_dev_ubirch_bigchaindb_tx'")
    logger.info("SQS_REGION -> AWS region (optional), default is 'eu-west-1'")
    logger.info("IPDB_APP_ID -> IPDB application id (optional), not set as default")
    logger.info("IPDB_APP_KEY -> IPDB application key (optional), not set as default")
    logger.info("BIG_CHAIN_DB_HOST -> bigChainDb host (optional), , default is 'http://localhost:9984'")
    logger.info("NUM_TRHEADS -> # of threads which poll new messages (optional), , default is 1")
    exit(1)

REGION = os.environ[sqsRegionKey] if sqsRegionKey in os.environ else 'eu-central-1'

sqs = boto3.resource('sqs', region_name=REGION)

inQueue = os.environ[sqsChainInKey] if sqsChainInKey in os.environ else "local_dev_ubirch_bigchaindb_in"
txQueue = os.environ[sqsChainTxKey] if sqsChainTxKey in os.environ else "local_dev_ubirch_bigchaindb_tx"

logger.info("inQueue: %s" % inQueue)
logger.info("txQueue: %s" % txQueue)

bigChainDbHost = os.environ[bigChainDbHostKey] if bigChainDbHostKey in os.environ else 'http://localhost:9984'
logger.info("current bigchaindb hosT: %s" % (bigChainDbHost))
numThreads = int(os.environ[numThreadsKey]) if numThreadsKey in os.environ else 1

tokens = {}
if ipdbAppIdKey in os.environ:
    tokens['app_id'] = os.environ[ipdbAppIdKey] if ipdbAppIdKey in os.environ else ""
    tokens['app_key'] = os.environ[ipdbAppKeyKey] if ipdbAppKeyKey in os.environ else ""

metadata = {'service': 'ubirchChainService'}
bdb = BigchainDB(bigChainDbHost, headers=tokens)

alice = generate_keypair()


class myThread(threading.Thread):
    def __init__(self, threadID, name):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name

    def run(self):
        main(inQueue)


def main(queue_name):
    """Continuously poll the queue for messages (jobs)."""
    queue = sqs.get_queue_by_name(QueueName=queue_name)
    while True:
        poll(queue=queue)


def poll(queue):
    messages = queue.receive_messages(MaxNumberOfMessages=10)  # Note: MaxNumberOfMessages default is 1.
    for m in messages:
        process_message(m)


def process_message(message):
    logger.debug("message body: %s" % message.body)
    try:
        success = anchor(payload=message.body)
        if success:  # processed ok
            message.delete()  # remove from queue
        else:  # an error of some kind
            message.change_visibility(VisibilityTimeout=1)  # dead letter or try again
    except:
        message.delete()
        logger.error("message processing error %s" % (message))


def sendTx(tx):
    sqs = boto3.resource('sqs', region_name=REGION)
    queue = sqs.get_queue_by_name(QueueName=txQueue)
    queue.send_message(MessageBody=tx)


def anchor(payload):
    logger.debug("payload: %s" % payload)

    jsonPayload = json.loads(payload)

    prepared_creation_tx = bdb.transactions.prepare(
        operation='CREATE',
        signers=alice.public_key,
        asset={'data': {'chaindata': jsonPayload}},
        metadata=metadata,
    )
    fulfilled_creation_tx = bdb.transactions.fulfill(prepared_creation_tx, private_keys=alice.private_key)
    sent_creation_tx = bdb.transactions.send(fulfilled_creation_tx)
    tx = fulfilled_creation_tx['id']

    if 'id' in jsonPayload:
        mid = jsonPayload['id']
        sendTx(json.dumps({"messageId": "%s" % mid, "tx": "%s" % tx}))
        logger.info("current mid: %s / tx: %s" % (mid, tx))
    else:
        logger.error("invalid message: %s" % payload)
    return True


logger.info("startinge %i threads" % numThreads)
for i in range(numThreads):
    thread = myThread(i, "Thread-%s" % (i))
    thread.start()

print ("Exiting Thread launcher")

main(inQueue)
