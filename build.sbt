name := """Junkyard"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))

parallelExecution in Test := false

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
    "junit" % "junit" % "4.6" % "test"
    , "org.specs2" %% "specs2-core" % "3.8.9" % "test"
    , "ch.qos.logback" % "logback-classic" % "1.2.3"
)

