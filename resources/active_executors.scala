import org.apache.spark.SparkContext

def currentActiveExecutors(sc: SparkContext): Seq[String] = {
 val allExecutors = sc.getExecutorMemoryStatus.map(_._1)
 val driverHost: String = sc.getConf.get("spark.driver.host")
 allExecutors.filter(! _.split(":")(0).equals(driverHost)).toList
}

currentActiveExecutors(sc)

