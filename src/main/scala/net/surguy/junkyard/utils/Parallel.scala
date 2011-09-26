package net.surguy.junkyard.utils

/**
 * http://stackoverflow.com/questions/1751953/concurrent-map-foreach-in-scala
 */
object Parallel {
  val cpus=java.lang.Runtime.getRuntime().availableProcessors
  import java.util.{Timer,TimerTask}
  def afterDelay(ms: Long)(op: =>Unit) = new Timer().schedule(new TimerTask {override def run = op},ms)
  def repeat(n: Int,f: Int=>Unit) = {
    import java.util.concurrent._
    val e=Executors.newCachedThreadPool //newFixedThreadPool(cpus+1)
    (0 until n).foreach(i=>e.execute(new Runnable {def run = f(i)}))
    e.shutdown
    e.awaitTermination(Long.MaxValue, TimeUnit.SECONDS)
  }
}
