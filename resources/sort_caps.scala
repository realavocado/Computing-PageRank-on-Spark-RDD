import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._


val capsDf = spark.read.option("header", "false").csv("hdfs://main:9000/dataset/caps/caps.csv")

val renamedDf = capsDf.withColumnRenamed("_c0", "year").withColumnRenamed("_c1", "serial")

val filteredDf = renamedDf.filter("year <= 2023")

val sortedDf = filteredDf.orderBy(col("year").desc, col("serial").asc)

sortedDf.show(false)

sortedDf.write.mode("overwrite").csv("hdfs://main:9000/dataset/caps_sorted")
