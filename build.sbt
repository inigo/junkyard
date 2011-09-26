name := "Junkyard"

version := "1.0"

organization := "surguy.net"

scalaVersion := "2.9.1"


parallelExecution in Test := false

// logLevel := Level.Warn

// only show stack traces up to the first sbt stack frame
traceLevel := 0

// Use an external Maven POM
externalPom()

resolvers += "Local Maven Repository" at "file://"+Path.userHome+"/.m2/repository"
