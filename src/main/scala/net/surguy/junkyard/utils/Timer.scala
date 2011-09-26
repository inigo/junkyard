package net.surguy.junkyard.utils

import java.io.PrintStream
import collection.mutable.HashMap
import java.lang.String

/**
 * 
 *
 * @author Inigo Surguy
 * @created Mar 23, 2010 8:38:06 PM
 */
class Timer(val ignoreFirst: Int = 0, val stages: Int = 1)(implicit val out: LoggingStream) {
  var count = 0
  val totals = new HashMap[String, Long]() {
    override def default(key: String) = 0
  }

  def time[T](label: String, body: () => T): T = {
    val timeBefore = System.nanoTime
    val result = body()
    val timeAfter = System.nanoTime
    val timeTaken = (timeAfter - timeBefore) / 1000000
    out.delegate.println(label+ ": "+timeTaken)
    count = count + 1
    if (count>ignoreFirst) totals(label) = totals(label)+timeTaken
    result
  }

  def showAverage() {
    if (count>ignoreFirst) {
      val totalCount = totals.values.sum / ((count/stages)-ignoreFirst)
      val separateCounts = totals.collect{ case (label,total) => label+" "+( total / ((count/stages)-ignoreFirst) ) }.mkString(", ")
      out.delegate.println("Average: %s divided into: %s".format(totalCount, separateCounts))
    } else out.delegate.println("No average recorded yet")
  }

}

object LoggingStream {
  implicit val defaultOutput = LoggingStream(Console.out)
}
case class LoggingStream(delegate: PrintStream)
