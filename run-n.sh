#!/bin/bash

JAR=target/scala-2.11/mountainrangepvp-assembly-1.0.jar
CLIENTS=${1:-1}

java -jar $JAR server ServerUser &

sleep 1


for (( i=1; i <= $CLIENTS; i++ ))
do
    echo "Client $i"
    java -jar $JAR client localhost "ClientUser $i" &
done


wait
