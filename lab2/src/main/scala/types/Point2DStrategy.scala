package types

import inface.UserTypeInterface
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.BufferedReader
import model.Point2D
import java.util.List

class Point2DStrategy extends UserTypeInterface[Point2D] {
  
  override def randomValue(random: java.util.random.RandomGenerator): Point2D = {
    new Point2D(
      random.nextInt(-10000, 10001) / 100.0,
      random.nextInt(-10000, 10001) / 100.0
    )
  }

  override def sampleValues(): List[Point2D] = {
    List.of(
      new Point2D(3, 4),
      new Point2D(1, 1),
      new Point2D(0, 2),
      new Point2D(5, 12),
      new Point2D(-3, 4),
      new Point2D(0, 0)
    )
  }

  override def typeName(): String = {
    "2D-точка"
  }

  override def create(): Point2D = {
    new Point2D()
  }

  override def clone(`object`: Point2D): Point2D = {
    new Point2D(`object`.getX(), `object`.getY())
  }

  override def parseValue(ss: String): Point2D = {
    val parts = ss.trim().split("\\s+")
    new Point2D(java.lang.Double.parseDouble(parts(0)), java.lang.Double.parseDouble(parts(1)))
  }

  override def readValue(in: InputStreamReader): Point2D = {
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

  override def compare(o1: Point2D, o2: Point2D): Int = {
    java.lang.Double.compare(o1.getDistance(), o2.getDistance())
  }

  override def toString(`object`: Point2D): String = {
    java.lang.String.format("Точка(%.2f; %.2f) [Дист: %.2f]", `object`.getX(), `object`.getY(), `object`.getDistance())
  }

  @throws[IOException]
  override def writeValue(`object`: Point2D, writer: BufferedWriter): Unit = {
    writer.write(`object`.getX() + " " + `object`.getY())
  }
}
