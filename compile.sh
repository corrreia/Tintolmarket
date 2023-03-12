#! /bin/bash

SERVER_JAVA_FILES="src/server/"
CLIENT_JAVA_FILES="src/client/"

SERVER_CLASS_FILES="bin/"
CLIENT_CLASS_FILES="bin/"

OUTPUT_DIR="out/"


if [ ! -d "$OUTPUT_DIR_SERVER" ]; then #create server bin directory if it doesn't exist
    echo "Creating output directory for server"
    mkdir -p $OUTPUT_DIR_SERVER
fi

if [ ! -d "$OUTPUT_DIR_CLIENT" ]; then #create client bin directory if it doesn't exist
    echo "Creating output directory for client"
    mkdir -p $OUTPUT_DIR_CLIENT
fi

echo "Compiling server files"
javac -d $SERVER_CLASS_FILES $SERVER_JAVA_FILES/*.java

echo "Compiling client files"
javac -d $CLIENT_CLASS_FILES $CLIENT_JAVA_FILES/*.java

if [ ! -d "$SERVER_CLASS_FILES" ]; then  #create output directory if it doesn't exist
    echo "Creating class directory for server"
    mkdir -p $SERVER_CLASS_FILES
fi

echo "Building server jar"
jar -cvfe "$OUTPUT_DIR/server.jar" server.Server -C "$SERVER_CLASS_FILES" .

echo "Building client jar"
jar -cvfe "$OUTPUT_DIR/client.jar" client.Client -C "$CLIENT_CLASS_FILES" .
