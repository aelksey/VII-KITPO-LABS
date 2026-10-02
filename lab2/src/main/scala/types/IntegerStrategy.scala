package types

import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.BufferedReader
import inface.UserTypeInterface
import java.util.List

class IntegerStrategy extends UserTypeInterface[java.lang.Integer] {
  
  override def randomValue(random: java.util.random.RandomGenerator): java.lang.Integer = {
    java.lang.Integer.valueOf(random.nextInt(-1000, 1001))
  }

  override def sampleValues(): List[java.lang.Integer] = {
    List.of(
      java.lang.Integer.valueOf(42),
      java.lang.Integer.valueOf(-7),
      java.lang.Integer.valueOf(15),
      java.lang.Integer.valueOf(0),
      java.lang.Integer.valueOf(100),
      java.lang.Integer.valueOf(-25)
    )
  }

  override def typeName(): String = {
    "Целое число"
  }

  override def create(): java.lang.Integer = {
    java.lang.Integer.valueOf(0)
  }

  override def clone(`object`: java.lang.Integer): java.lang.Integer = {
    java.lang.Integer.valueOf(`object`.intValue())
  }

  override def parseValue(ss: String): java.lang.Integer = {
    java.lang.Integer.valueOf(java.lang.Integer.parseInt(ss.trim()))
  }

  override def readValue(in: InputStreamReader): java.lang.Integer = {
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

  override def compare(o1: java.lang.Integer, o2: java.lang.Integer): Int = {
    java.lang.Integer.compare(o1.intValue(), o2.intValue())
  }

  override def toString(`object`: java.lang.Integer): String = {
    java.lang.String.valueOf(`object`)
  }

  @throws[IOException]
  override def writeValue(`object`: java.lang.Integer, writer: BufferedWriter): Unit = {
    writer.write(java.lang.String.valueOf(`object`))
  }
}
