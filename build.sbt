import sbtprotobuf.{ProtobufPlugin=>PB}


organization := "me.lachlanap"

name := "mountainrangepvp"

version := "1.0"


scalaVersion := "2.11.5"


libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "com.badlogicgames.gdx" % "gdx" % "1.5.3",
  "com.badlogicgames.gdx" % "gdx-backend-lwjgl" % "1.5.3",
  "com.badlogicgames.gdx" % "gdx-freetype" % "1.5.3",
  "com.badlogicgames.gdx" % "gdx-freetype-platform" % "1.5.3" classifier "natives-desktop",
  "com.badlogicgames.gdx" % "gdx-platform" % "1.5.3" classifier "natives-desktop",
  "io.netty" % "netty-all" % "4.0.27.Final",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)


scalacOptions ++= Seq(
  "-Xlint",
  "-Ywarn-dead-code",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-encoding", "UTF-8",
  "-target:jvm-1.7"
)

javacOptions ++= Seq(
  "-Xlint:deprecation",
  "-Xlint:unchecked",
  "-source", "1.7",
  "-target", "1.7"
)


fork in run := true

mainClass in (Compile, run) := Some("mountainrangepvp.Main")

mainClass in assembly := Some("mountainrangepvp.Main")

