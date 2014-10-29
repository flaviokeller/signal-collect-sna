import AssemblyKeys._ 
assemblySettings

name := "signal-collect-sna"
 
version := "1.0"
 
organization := "com.signalcollect"
 
scalaVersion := "2.11.2"

scalacOptions ++= Seq("-optimize", "-Yinline-warnings", "-feature", "-deprecation", "-Xelide-below", "INFO" , "-target:jvm-1.6")

javacOptions ++= Seq("-source","1.6","-target","1.6")

parallelExecution in Test := false

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

EclipseKeys.withSource := true

jarName in assembly := "signal-collect-sna-1.0.jar"

/** Dependencies */
libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % "2.11.2" % "compile",
  "junit" % "junit" % "4.10" % "test",
  "org.jfree" % "jfreechart" % "1.0.14" % "compile"
  )