import sbt._

object Dependencies {
  val sparkVersion = "2.1.0"

  def spark(module: String): ModuleID = {
    "org.apache.spark" %% s"spark-$module" % sparkVersion
  }

  def compileScope(deps: ModuleID*): Seq[ModuleID] = deps.map(_ % "compile")
  def optionalScope(deps: ModuleID*): Seq[ModuleID] = deps.map(_ % "optional")
  def providedScope(deps: ModuleID*): Seq[ModuleID] = deps.map(_ % "provided")
  def testScope(deps: ModuleID*): Seq[ModuleID] = deps.map(_ % "test")
}
