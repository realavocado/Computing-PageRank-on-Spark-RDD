// https://spark.apache.org/examples.html
val count = sc.parallelize(1 to 10000000).filter { _ =>
  val x = math.random
  val y = math.random
  x*x + y*y < 1
}.count()
println(s"Pi is roughly ${4.0 * count / 10000000}")
