#!/bin/bash

JAR=target/scala-2.11/mountainrangepvp-assembly-1.0.jar
CLIENTS=${1:-1}

trap 'jobs -p | xargs kill' EXIT


java -jar $JAR server ServerUser &

sleep 1


for (( i=1; i <= $CLIENTS; i++ ))
do
    java -jar $JAR client localhost "ClientUser $i" &
done


while pgrep -P "$BASHPID" > /dev/null; do
      wait
done
