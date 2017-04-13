package org.apache.spark.sql.catalyst.expressions

import java.sql.Timestamp

import com.vertica.jdbc.kv.VHash
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.analysis.TypeCheckResult
import org.apache.spark.sql.catalyst.expressions.codegen.CodegenFallback
import org.apache.spark.sql.types._
import org.apache.spark.unsafe.types.UTF8String

case class VHashExpr(children: Seq[Expression]) extends Expression with CodegenFallback {
  @transient private lazy val vhash = new VHash()
  @transient private lazy val converters = children.map(child => (child, createConverter(child.dataType)))

  override def eval(input: InternalRow): Any = {
    vhash.reset()

    converters.foreach {
      case (child, converter) =>
        converter(child.eval(input))
    }

    vhash.getHash
  }

  override def nullable: Boolean = false
  override def dataType: DataType = LongType

  override def checkInputDataTypes(): TypeCheckResult = {
    val validDataTypes = Seq(
      BinaryType,
      ByteType,
      ShortType,
      IntegerType,
      LongType,
      FloatType,
      DoubleType,
      StringType,
      BooleanType,
      TimestampType)

    children.forall(child => validDataTypes.contains(child.dataType)) match {
      case true   => TypeCheckResult.TypeCheckSuccess
      case false  => TypeCheckResult.TypeCheckFailure(
        s"invalid data type passed to vhash, supported data types include: ${validDataTypes.mkString(", ")}")
    }
  }

  private def createConverter(dataType: DataType): (Any) => Unit = {
    dataType match {
      case BinaryType => {
        case null => vhash.addNull()
        case bytes: Array[Byte] => vhash.addBytes(bytes)
      }

      case ByteType => {
        case null => vhash.addNull()
        case byte: Byte => vhash.addLong(byte.toLong)
      }

      case ShortType => {
        case null => vhash.addNull()
        case short: Short => vhash.addLong(short.toLong)
      }

      case IntegerType => {
        case null => vhash.addNull()
        case int: Integer => vhash.addLong(int.toLong)
      }

      case LongType => {
        case null => vhash.addNull()
        case long: Long => vhash.addLong(long)
      }

      case FloatType => {
        case null => vhash.addDoubleNull()
        case float: Float => vhash.addDouble(float.toDouble)
      }

      case DoubleType => {
        case null => vhash.addDoubleNull()
        case double: Double => vhash.addDouble(double)
      }

      case StringType => {
        case null => vhash.addNull()
        case str: String => vhash.addString(str)
        case utf: UTF8String => vhash.addString(utf.toString)
      }

      case BooleanType => {
        case null => vhash.addBooleanNull()
        case bool: Boolean => vhash.addBoolean(bool)
      }

      case TimestampType => {
        case null => vhash.addNull()
        case ts: Timestamp => vhash.addTimestamp(ts.getTime)
        case long: Long => vhash.addTimestamp(long)
      }
    }
  }
}
