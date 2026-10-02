package types

import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.BufferedReader
import inface.UserTypeInterface
import java.util.List

class StringStrategy extends UserTypeInterface[String] {
  
  override def randomValue(random: java.util.random.RandomGenerator): String = {
    val alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val length = random.nextInt(3, 13)
    val value = new java.lang.StringBuilder(length)
    for (i <- 0 until length) {
      value.append(alphabet.charAt(random.nextInt(alphabet.length())))
    }
    value.toString()
  }

  override def sampleValues(): List[String] = {
    List.of("Гамма", "Альфа", "Бета", "Дельта", "Омега", "Лямбда")
  }

  override def deserializeValue(value: String): String = {
    value
  }

  override def typeName(): String = {
    "Строка"
  }

  override def create(): String = {
    ""
  }

  override def clone(`object`: String): String = {
    if (`object` == null) {
      null
    } else {
      new java.lang.String(`object`)
    }
  }

  override def parseValue(ss: String): String = {
    ss.trim()
  }

  override def readValue(in: InputStreamReader): String = {
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

  override def compare(o1: String, o2: String): Int = {
    if ((o1 == null) && (o2 == null)) {
      0
    } else {
      if (o1 == null) {
        -1
      } else {
        if (o2 == null) {
          1
        } else {
          o1.compareTo(o2)
        }
      }
    }
  }

  override def toString(`object`: String): String = {
    if (`object` != null) {
      `object`
    } else {
      ""
    }
  }

  @throws[IOException]
  override def writeValue(`object`: String, writer: BufferedWriter): Unit = {
    writer.write(toString(`object`))
  }
}
