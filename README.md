# spark-vhash

Exposes Vertica's hashing function to Spark. Although this may seem like a simple utility, it empowers users to do the right thing when processing data. For example, assume you are processing dimensions and facts. You produce a surrogate key for your dimension, and then transform your facts during ingest to have a reference to said surrogate key. Although it feels like a win, you have introduced many problems. If a dimension doesn't exist, this is now a problem you must consider and deal with. Perhaps the dimension hasn't reached your system yet due to latency, how do you know when it arrives and deal with this retroactively? When dealing with natural keys, it is often a much more fruitful exercise to hash the natural keys between your dimensions and facts. The facts consequently become eventually consistent with the dimension, removing the need to cope with missing dimensions, etc. These issues simply become an aspect of reports over the facts. In a simple example, if you do not want your analysts to see the facts with no corresponding dimension, simply give them access to a view which filters out the facts which have not reconciled with the dimension(s). Similarly, you have potentially removed dependencies in workflow management from your ETL, allowing fact and dimension processing to be fully parallel and completely unaware of one another. Less dependencies, less problems. Did you buy that fancy workflow scheduler because you needed it or are you really just paying someone to make your life more complicated?

## usage

~~~scala
val spark: SparkSession = _

import com.cbowden.spark.vhash._
spark.includeVHash()

spark.sql("select vhash(1, false, 'foo')").show()
~~~

## todo

* implement codegen over VHashExpr
* implement implicits for vhash function to be used as a literal in scala vs. just a sql binding
* implement better coverage of underlying spark data types
* provide unit tests using random data generation for different combinatorials of data types, etc.
* provide integration tests which physically communicate with different vertica backends to validate vhash integration
