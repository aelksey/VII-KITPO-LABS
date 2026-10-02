package serialize

import core.CustomList
import inface.SerializeStrategyInterface
import inface.UserTypeInterface
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.util.ArrayList
import java.util.List
import scala.jdk.CollectionConverters._

class PlainTxtSerializeStrategy extends SerializeStrategyInterface {
  
  override def fileExtension(): String = "txt"
  override def formatName(): String = "Plain Text"

  private val HEADER: String = "CUSTOM_LIST_TEXT_V1"

  @throws[IOException]
  override def save[T](filename: String, list: CustomList[T], `type`: UserTypeInterface[T]): Unit = {
    val lines: List[String] = new ArrayList[String]()
    lines.add(HEADER)
    
    list.toArrayList().asScala.foreach { value => {
      lines.add(JsonStrings.quote(`type`.serializeValue(value)))
    }}
    
    Files.write(Path.of(filename), lines, StandardCharsets.UTF_8)
  }

  @throws[IOException]
  override def load[T](filename: String, list: CustomList[T], `type`: UserTypeInterface[T]): Unit = {
    val lines: List[String] = Files.readAllLines(Path.of(filename), StandardCharsets.UTF_8)
    val encoded: Boolean = !lines.isEmpty && HEADER.equals(lines.get(0))
    val loaded: CustomList[T] = new CustomList[T](list.getBlockCapacity())
    
    try {
      val startIdx: Int = if (encoded) { 1 } else { 0 }
      
      for (i <- startIdx until lines.size()) {
        var raw: String = lines.get(i)
        if (encoded) {
          val decoded: List[String] = JsonStrings.parse("[" + raw + "]")
          if (decoded.size() != 1) {
            throw new IOException("Ожидалось одно значение в строке")
          }
          raw = decoded.get(0)
        }
        loaded.add(`type`.deserializeValue(raw))
      }
    } catch {
      case e: IllegalArgumentException => {
        throw new IOException("Значение не соответствует выбранному типу", e)
      }
    }
    
    list.clear()
    loaded.forEach { item => {
      list.add(item)
    }}
  }
}
