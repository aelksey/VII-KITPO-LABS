package serialize

import core.CustomList
import inface.SerializeStrategyInterface
import inface.UserTypeInterface
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.util.ArrayList
import scala.jdk.CollectionConverters._

class JsonSerializeStrategy extends SerializeStrategyInterface {
  
  override def fileExtension(): String = "json"
  override def formatName(): String = "JSON"

  @throws[IOException]
  override def save[T](filename: String, list: CustomList[T], `type`: UserTypeInterface[T]): Unit = {
    val values = new ArrayList[String]()
    list.toArrayList().asScala.foreach { item => {
      values.add(JsonStrings.quote(`type`.serializeValue(item)))
    }}
    val jsonContent = "[\n" + java.lang.String.join(",\n", values) + "\n]\n"
    Files.writeString(Path.of(filename), jsonContent, StandardCharsets.UTF_8)
  }

  @throws[IOException]
  override def load[T](filename: String, list: CustomList[T], `type`: UserTypeInterface[T]): Unit = {
    val path = Path.of(filename)
    if (Files.notExists(path)) {
      list.clear()
    } else {
      val loaded = new CustomList[T](list.getBlockCapacity())
      try {
        val parsedLines = JsonStrings.parse(Files.readString(path, StandardCharsets.UTF_8))
        parsedLines.asScala.foreach { value => {
          loaded.add(`type`.deserializeValue(value))
        }}
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
}
