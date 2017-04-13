package com.cbowden.spark.vhash

import org.apache.spark.sql.SparkSession

object SparkDriver {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("vhash")
      .getOrCreate()

    spark.includeVHash()

    spark.sql("select vhash(1, false, 'foo')").show()
  }
}
