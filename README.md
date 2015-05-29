Mountain Range PvP
==================

A 2D TPS PvP shooter game, played on a heightmap that was originally a mountain
range (now it's just hills).

Written in a mix of Java and Scala, with LibGDX.

Compiling
=========

Uses SBT to build.

To build the jar:

```bash
$ sbt assembly
```

and the jar will be:

`target/scala-${SCALA_VERSION}/mountainrangepvp-assembly-${MRPVP_VERSION}.jar`



Running
=======

Run the executable jar, or with SBT `sbt run`.

Start a server, or connect to an existing LAN server. LAN servers should
automatically be picked up by the multicast ping system.

To skip the launcher, use the command line options:

`java -jar mountainrangepvp.jar <mode>`

where mode is one of:

```
client <server ip> <username>
server <username>
```
