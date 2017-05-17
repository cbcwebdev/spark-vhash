import Dependencies._

scalaVersion in ThisBuild := "2.11.8"
updateOptions in ThisBuild := updateOptions.value.withCachedResolution(true)

lazy val vhash = (project in file("."))
  .settings(organization := "com.cbowden")
  .settings(name := "spark-vhash")
  .settings(version := "0.0.1-SNAPSHOT")
  .settings(libraryDependencies ++= providedScope(spark("sql")))
