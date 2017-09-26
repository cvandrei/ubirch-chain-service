import websocket
import os

ipdbAppIdKey = 'IPDB_APP_ID'
ipdbAppKeyKey = 'IPDB_APP_KEY'

# websocketurl = "wss://test.ipdb.io:9985/api/v1/streams/valid_transactions"
websocketurl = "ws://localhost:9985/api/v1/streams/valid_transactions"

bcdbHeaders = {}

if ipdbAppIdKey in os.environ and ipdbAppKeyKey in os.environ:
    bcdbHeaders['APP_ID'] = os.environ[ipdbAppIdKey]
    bcdbHeaders['APP_KEY'] = os.environ[ipdbAppKeyKey]


def on_message(ws, message):
    print(message)


def on_error(ws, error):
    print(error)


def on_close(ws):
    print("### closed ###")


if __name__ == "__main__":
    websocket.enableTrace(True)
    print("using headers: %s" % bcdbHeaders)
    ws = websocket.WebSocketApp(websocketurl,
                                on_message=on_message,
                                on_error=on_error,
                                on_close=on_close,
                                header=bcdbHeaders)
    ws.run_forever()
