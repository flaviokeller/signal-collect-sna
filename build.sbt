import AssemblyKeys._ 
assemblySettings

name := "SignalCollectSNA"
 
version := "0.1-SNAPSHOT"
 
organization := "com.signalcollect"
 
scalaVersion := "2.10.4"

scalacOptions ++= Seq("-optimize", "-Yinline-warnings", "-feature", "-deprecation", "-Xelide-below", "INFO" )

parallelExecution in Test := false

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

EclipseKeys.withSource := true

jarName in assembly := "signal-collect-sna-0.1-SNAPSHOT.jar"

/** Dependencies */
libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % "2.10.4" % "compile",
  "junit" % "junit" % "4.10" % "test",
  "org.jfree" % "jfreechart" % "1.0.14" % "compile"
  )