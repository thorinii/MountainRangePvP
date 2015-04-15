#!/bin/sh

sbt "run server ServerUser" &

sleep 1

sbt "run client localhost ClientUser"

