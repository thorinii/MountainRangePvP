#!/bin/sh

JAR=target/scala-2.11/mountainrangepvp-assembly-1.0.jar
java -jar $JAR server ServerUser &

sleep 1

java -jar $JAR client localhost ClientUser

