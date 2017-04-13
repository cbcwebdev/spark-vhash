import Dependencies._

scalaVersion in ThisBuild := "2.11.8"
updateOptions in ThisBuild := updateOptions.value.withCachedResolution(true)

lazy val vhash = (project in file("."))
  .settings(name := "spark-vhash")
  .settings(libraryDependencies ++= providedScope(spark("sql")))
