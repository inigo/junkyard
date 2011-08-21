package net.surguy.junkyard.utils

import org.specs.SpecificationWithJUnit
import java.io.{ByteArrayOutputStream, PrintStream}

/**
 * 
 *
 * @author Inigo Surguy
 * @created Apr 2, 2010 7:56:19 AM
 */

class TimerTest extends SpecificationWithJUnit {
  def pause(time: Int) { Thread.sleep(time) }

  // These definitions put the implicit LoggingStream in scope so it can be used by the Timer for output - i.e. so
  // output can be tested as a string, rather than going to the console
  var out : ByteArrayOutputStream = null
  def loggingStream = new StreamTimeReporter(new PrintStream(out))
  implicit def pimpLastLine(stream: ByteArrayOutputStream) = new { def lastLine = stream.toString.split("\n").last }

  "timing functions" should {
    doBefore { out = new ByteArrayOutputStream() }
    "display the time taken for a function" in {
      val timer = new Timer(out = loggingStream )
      timer.time("test", () => pause(100))
      out.toString.length must beGreaterThan(1)
      out.toString.replaceAll("[^\\d]","").toInt must beCloseTo(100, 20)
    }
    "display the time taken for several separate functions" in {
      val timer = new Timer(stages = 3, out = loggingStream )
      timer.time("short", () => pause(50))
      timer.time("longer", () => pause(100))
      timer.time("longest", () => pause(150))
      timer.showAverage()
      out.toString.length must beGreaterThan(1)
      println(out)
      val SplitAverage = "Average: (\\d+) divided into: longer (\\d+), longest (\\d+), short (\\d+)".r
      val SplitAverage(total, longer, longest, short) = out.lastLine
      println("Total is "+total)
      total.toInt must beCloseTo(300,30)
      short.toInt must beCloseTo(50,5)
      longer.toInt must beCloseTo(100,10)
      longest.toInt must beCloseTo(150,15)
    }
  }


}