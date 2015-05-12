[![Stories in Ready](https://badge.waffle.io/thorinii/MountainRangePvP.png?label=ready&title=Ready)](https://waffle.io/thorinii/MountainRangePvP)
Mountain Range PvP
==================

A 2D player vs player game, played on a heightmap that was originally a mountain range (now its just hills). 

For a precompiled version download [1.0](http://devlog.terrifictales.net/wp-content/uploads/2013/03/wpid-mountainrangepvp-v1.0.jar), or download any other from [this blog](http://devlog.terrifictales.net/). The precompiled jars are compiled, packaged, and uploaded by my [Project Builder](http://github.com/ThorinII/ProjectBuilder).


Compiling
=========

Download the source code. All the libraries *should* be in the libs folder, so that running the command:

```bash
$ ant jar
```

should properly compile the project. Note the source code is written using __Java 1.7__.


Running
=======

Run the jar file as you normally would. Start a server, or connect to an existing LAN server. LAN servers should automatically be picked up by the multicast ping system.
