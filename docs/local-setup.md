## Local Setup

1. Installation
 
  1.1. Dependencies

    * Ubuntu 16.04

      ```
      sudo apt install python3-pip libssl-dev
      sudo -H pip3 install -r bigChainDbStore/requirements.txt
      ```

    * MacOS

      ```
      tbd
      ```

  1.2. BigchainDB

    [BigchainDB Quickstart](https://docs.bigchaindb.com/projects/server/en/latest/quickstart.html)

2. Update

  2.1 BigchainDB

    sudo -H pip3 install --upgrade bigchaindb bigchaindb_driver

3. Start Environment

  3.1. BigchainDB
  
    Follow the instruction in: [BigchainDB Quickstart](https://docs.bigchaindb.com/projects/server/en/latest/quickstart.html).

  3.2. Python Wrapper around BigchainDB

    This Python wrapper listens on a queue for new transactions to store in BigchainDB.
    
    ```
    export AWS_ACCESS_KEY_ID={YOUR AWS ACCESS KEY}
    export AWS_SECRET_ACCESS_KEY={YOUR AWS SECRET KEY}
    python3 bigChainDbStore/src/bigChainDbStore.py
    ```

  3.3. Start Server

    ```
    export AWS_ACCESS_KEY_ID={YOUR AWS ACCESS KEY}
    export AWS_SECRET_ACCESS_KEY={YOUR AWS SECRET KEY}
    ./sbt server/run
    ```

4. Generate Test Data

  4.1. Directly to BigchainDB
  
    ```
    export AWS_ACCESS_KEY_ID={YOUR AWS ACCESS KEY}
    export AWS_SECRET_ACCESS_KEY={YOUR AWS SECRET KEY}
    python3 bigChainDbStore/src/bigChainDbTester.py
    ```

  4.2. Device Data Through chain-service
  
    ```
    export AWS_ACCESS_KEY_ID={YOUR AWS ACCESS KEY}
    export AWS_SECRET_ACCESS_KEY={YOUR AWS SECRET KEY}
    python3 bigChainDbStore/src/chainServiceDeviceMsgTester.py
    ```


## Create Docker Image

    ./goBuild.sh assembly
