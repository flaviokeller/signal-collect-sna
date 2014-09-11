import sbt._
import Keys._

object SNABuild extends Build {
  lazy val scCore = ProjectRef(file("../signal-collect"), id = "signal-collect")
  val scSignalCollectSNA = Project(id = "signal-collect-sna",
    base = file(".")) dependsOn (scCore)
}