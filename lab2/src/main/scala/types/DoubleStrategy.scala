package types

import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.BufferedReader
import inface.UserTypeInterface
import java.util.List

class DoubleStrategy extends UserTypeInterface[java.lang.Double] {
  
  override def randomValue(random: java.util.random.RandomGenerator): java.lang.Double = {
    java.lang.Double.valueOf(random.nextInt(-100000, 100001) / 100.0)
  }

  override def sampleValues(): List[java.lang.Double] = {
    List.of(
      java.lang.Double.valueOf(3.14),
      java.lang.Double.valueOf(-2.5),
      java.lang.Double.valueOf(0.125),
      java.lang.Double.valueOf(0.0),
      java.lang.Double.valueOf(10.75),
      java.lang.Double.valueOf(-8.25)
    )
  }

  override def typeName(): String = {
    "Вещественное число"
  }

  override def create(): java.lang.Double = {
    java.lang.Double.valueOf(0.0)
  }

  override def clone(`object`: java.lang.Double): java.lang.Double = {
    if (`object` == null) {
      null
    } else {
      java.lang.Double.valueOf(`object`.doubleValue())
    }
  }

  override def parseValue(ss: String): java.lang.Double = {
    java.lang.Double.valueOf(java.lang.Double.parseDouble(ss.trim()))
  }

  override def readValue(in: InputStreamReader): java.lang.Double = {
    val br = new BufferedReader(in)
    try {
      val line = br.readLine()
      if (line == null) {
        null
      } else {
        parseValue(line)
      }
    } catch {
      case e: Exception => {
        null
      }
    } finally {
      br.close()
    }
  }

  override def compare(o1: java.lang.Double, o2: java.lang.Double): Int = {
    java.lang.Double.compare(o1.doubleValue(), o2.doubleValue())
  }

  override def toString(`object`: java.lang.Double): String = {
    java.lang.String.valueOf(`object`)
  }

  @throws[IOException]
  override def writeValue(`object`: java.lang.Double, writer: BufferedWriter): Unit = {
    writer.write(java.lang.String.valueOf(`object`))
  }
}
