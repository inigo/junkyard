package net.surguy.junkyard.utils

import java.io.PrintStream

import org.slf4j.Logger

import scala.collection.mutable

/**
  * Time a function - particularly when called repeatedly - and log the total and average times taken.
  */
class Timer(ignoreFirst: Int = 0, stages: Int = 1, out: TimeReporter) {
  var count = 0
  val totals = new mutable.HashMap[String, Long]() {
    override def default(key: String) = 0
  }

  def time[T](label: String, body: () => T): T = {
    val timeBefore = System.nanoTime
    val result = body()
    val timeAfter = System.nanoTime
    val timeTaken = (timeAfter - timeBefore) / 1000000
    out.println(label+ ": "+timeTaken)
    count = count + 1
    if (count>ignoreFirst) totals(label) = totals(label)+timeTaken
    result
  }

  def showAverage() {
    if (count>ignoreFirst) {
      val totalCount = totals.values.sum / ((count/stages)-ignoreFirst)
      val separateCounts = totals.collect{ case (label,total) => label+" "+( total / ((count/stages)-ignoreFirst) ) }.mkString(", ")
      out.println("Average: %s divided into: %s".format(totalCount, separateCounts))
    } else out.println("No average recorded yet")
  }

}

abstract class TimeReporter {
  def println(s: String)
}

class StreamTimeReporter(stream: PrintStream) extends TimeReporter {
  def println(s: String) { stream.println(s) }
}

class LogTimeReporter(log: Logger) extends TimeReporter {
  def println(s: String) { log.debug(s) }
}