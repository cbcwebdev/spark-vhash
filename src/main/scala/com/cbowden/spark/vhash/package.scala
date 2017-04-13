package com.cbowden.spark

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.analysis.FunctionRegistryOps
import org.apache.spark.sql.catalyst.expressions.VHashFunction

package object vhash {
  implicit class SparkSessionOps(spark: SparkSession) {
    def includeVHash(): SparkSession = {
      FunctionRegistryOps(spark)
        .register(VHashFunction.registration)

      spark
    }
  }
}
