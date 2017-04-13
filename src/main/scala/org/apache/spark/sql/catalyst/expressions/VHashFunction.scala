package org.apache.spark.sql.catalyst.expressions

import org.apache.spark.sql.catalyst.analysis.FunctionRegistry.FunctionBuilder

object VHashFunction extends FunctionBuilder {
  private val FunctionName = "vhash"

  override def apply(children: Seq[Expression]): Expression = {
    VHashExpr(children)
  }

  val registration: (String, (ExpressionInfo, FunctionBuilder)) = {
    val info = new ExpressionInfo(
      classOf[VHashExpr].getCanonicalName,
      null,
      FunctionName,
      "_FUNC_(col1, ...) - hash referenced columns using vertica's hash function",
      ""
    )

    (FunctionName, (info, this))
  }
}
