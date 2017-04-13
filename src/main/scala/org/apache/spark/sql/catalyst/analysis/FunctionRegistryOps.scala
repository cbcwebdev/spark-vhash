package org.apache.spark.sql.catalyst.analysis

import org.apache.spark.sql.catalyst.analysis.FunctionRegistry._
import org.apache.spark.sql.catalyst.expressions.{Expression, ExpressionDescription, ExpressionInfo}
import org.apache.spark.sql.{AnalysisException, SparkSession}

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

object FunctionRegistryOps {
  def apply(spark: SparkSession): FunctionRegistryOps = {
    new FunctionRegistryOps(spark.sessionState.functionRegistry)
  }
}

class FunctionRegistryOps private[analysis](functions: FunctionRegistry) {
  def register(definition: (String, (ExpressionInfo, FunctionBuilder))): Unit = {
    functions.registerFunction(definition._1, definition._2._1, definition._2._2)
  }

  def register[T <: Expression : ClassTag](name: String): Unit = {
    register(name, expression[T](name))
  }

  /** See usage above. */
  private def expression[T <: Expression](name: String)(implicit tag: ClassTag[T]): (ExpressionInfo, FunctionBuilder) = {
    // See if we can find a constructor that accepts Seq[Expression]
    val varargCtor = Try(tag.runtimeClass.getDeclaredConstructor(classOf[Seq[_]])).toOption
    val builder = (expressions: Seq[Expression]) => {
      if (varargCtor.isDefined) {
        // If there is an apply method that accepts Seq[Expression], use that one.
        Try(varargCtor.get.newInstance(expressions).asInstanceOf[Expression]) match {
          case Success(e) => e
          case Failure(e) =>
            // the exception is an invocation exception. To get a meaningful message, we need the
            // cause.
            throw new AnalysisException(e.getCause.getMessage)
        }
      } else {
        // Otherwise, find a constructor method that matches the number of arguments, and use that.
        val params = Seq.fill(expressions.size)(classOf[Expression])
        val f = Try(tag.runtimeClass.getDeclaredConstructor(params : _*)) match {
          case Success(e) =>
            e
          case Failure(e) =>
            throw new AnalysisException(s"Invalid number of arguments for function $name")
        }
        Try(f.newInstance(expressions : _*).asInstanceOf[Expression]) match {
          case Success(e) => e
          case Failure(e) =>
            // the exception is an invocation exception. To get a meaningful message, we need the
            // cause.
            throw new AnalysisException(e.getCause.getMessage)
        }
      }
    }

    (expressionInfo[T](name), builder)
  }

  private def expressionInfo[T <: Expression : ClassTag](name: String): ExpressionInfo = {
    val clazz = scala.reflect.classTag[T].runtimeClass
    val df = clazz.getAnnotation(classOf[ExpressionDescription])
    if (df != null) {
      new ExpressionInfo(clazz.getCanonicalName, null, name, df.usage(), df.extended())
    } else {
      new ExpressionInfo(clazz.getCanonicalName, name)
    }
  }
}
