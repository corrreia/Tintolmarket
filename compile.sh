#! /bin/bash

SERVER_JAVA_FILES="TintoIMarketServer/src/main/"
CLIENT_JAVA_FILES="TintoIMarket/src/main/"

SERVER_CLASS_FILES="TintoIMarketServer/bin/"
CLIENT_CLASS_FILES="TintoIMarket/bin/"

OUTPUT_DIR="out/"


if [ ! -d "$SERVER_CLASS_FILES" ]; then #create server bin directory if it doesn't exist
    echo "Creating bin directory for server"
    mkdir -p $SERVER_CLASS_FILES
fi

if [ ! -d "$CLIENT_CLASS_FILES" ]; then #create client bin directory if it doesn't exist
    echo "Creating bin directory for client"
    mkdir -p $CLIENT_CLASS_FILES
fi

echo "Compiling server files"
javac  $SERVER_JAVA_FILES/*.java -d $SERVER_CLASS_FILES

echo "Compiling client files"
javac $CLIENT_JAVA_FILES/*.java -d $CLIENT_CLASS_FILES

if [ ! -d "$OUTPUT_DIR" ]; then 
    echo "Creating bin directory for client"
    mkdir -p $OUTPUT_DIR
fi

echo "Building server jar"
jar -cvfe "$OUTPUT_DIR/server.jar" server.Server -C "$SERVER_CLASS_FILES" .

echo "Building client jar"
jar -cvfe "$OUTPUT_DIR/client.jar" client.Client -C "$CLIENT_CLASS_FILES" .
