#!/usr/bin/python3

import os
import boto3
import json
import logging

from bigchaindb_driver import BigchainDB
from bigchaindb_driver.crypto import generate_keypair

import threading
from bigchaindb.backend import connect, query

FORMAT = '%(asctime)-15s [%(levelname)s] %(message)s'
logging.basicConfig(format=FORMAT)
logger = logging.getLogger('bigChainDbStore')
logger.setLevel(logging.DEBUG)

ipdbAppIdKey = 'IPDB_APP_ID'
ipdbAppKeyKey = 'IPDB_APP_KEY'
bigChainDbHostKey = 'BIG_CHAIN_DB_HOST'

bigChainDbHost = os.environ[bigChainDbHostKey] if bigChainDbHostKey in os.environ else 'http://localhost:9984'
logger.info("current bigchaindb hosT: %s" % (bigChainDbHost))

tokens = {}
if ipdbAppIdKey in os.environ:
    tokens['app_id'] = os.environ[ipdbAppIdKey] if ipdbAppIdKey in os.environ else ""
    tokens['app_key'] = os.environ[ipdbAppKeyKey] if ipdbAppKeyKey in os.environ else ""

metadata = {'service': 'ubirchChainService'}

bdb = BigchainDB(bigChainDbHost, headers=tokens)

tx = bdb.transactions.retrieve(txid='a8f7306f6b304f90cc2db4c0eb341a782660a44a251706689d7144a0791149bd', headers=tokens)
print(tx)

c = bdb.transport.pool.connections

print(c)
exit(0)
