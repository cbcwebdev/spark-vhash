import Dependencies._

lazy val vhash = (project in file("."))
  .settings(name := "spark-vhash")
  .settings(libraryDependencies ++= providedScope(spark("sql")))
