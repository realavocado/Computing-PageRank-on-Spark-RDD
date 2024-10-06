import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import scala.math.abs

  val d = 0.85
  val tol = 0.0001

  val edgesDf = spark.read.option("header", "false").csv("hdfs://main:9000/dataset/pagerank/pagerank.csv")
  val renamedDf = edgesDf.withColumnRenamed("_c0", "from").withColumnRenamed("_c1", "to")

  val links = renamedDf.rdd.map(row => (row.getString(0).toInt, row.getString(1).toInt)).groupByKey().cache()

  val fromNodes = renamedDf.select("from").distinct().rdd.map(r => r.getString(0).toInt)
  val toNodes = renamedDf.select("to").distinct().rdd.map(r => r.getString(0).toInt)
  val nodes = fromNodes.union(toNodes).distinct().map(node => (node, 1.0)).cache()

  val totalNodes = nodes.count()

  var ranks = nodes.mapValues(_ => 1.0 / totalNodes).cache()

  var diff = Double.MaxValue

  while (diff > tol) {
    val contributions = links.join(ranks).flatMap { case (node, (outgoingLinks, rank)) =>
      if (outgoingLinks.isEmpty) {
        List.empty[(Int, Double)]
      } else {
        outgoingLinks.map(dest => (dest, rank / outgoingLinks.size))
      }
    }

    val danglingNodes = nodes.subtractByKey(links)
    val danglingRank = ranks.join(danglingNodes).values.map(_._1).sum()
    val danglingContribution = danglingRank / totalNodes

    val newRanks = contributions
      .reduceByKey(_ + _)
      .mapValues(contribution => (1 - d) / totalNodes + d * (contribution + danglingContribution))

    val allRanks = nodes.leftOuterJoin(newRanks).mapValues {
      case (_, Some(newRank)) => newRank
      case (_, None) => (1 - d) / totalNodes + d * danglingContribution
    }

    diff = ranks.join(allRanks).map { case (_, (oldRank, newRank)) => abs(newRank - oldRank) }.sum()

    ranks = allRanks.cache()
  }

  val sortedRanks = ranks.sortBy({ case (node, rank) => (-rank, node) })

  val ranksDf = sortedRanks.map { case (node, rank) => (node, f"$rank%.3f") }.toDF("node", "pagerank")

  ranksDf.write.mode("overwrite").csv("hdfs://main:9000/dataset/pagerank_results")
