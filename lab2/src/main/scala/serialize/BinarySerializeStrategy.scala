package serialize

import java.io._
import core.CustomList
import inface.SerializeStrategyInterface
import inface.UserTypeInterface
import scala.jdk.CollectionConverters._

class BinarySerializeStrategy extends SerializeStrategyInterface {
  
  override def fileExtension(): String = "bin"
  override def formatName(): String = ".bin"

  @throws[IOException]
  override def save[T](filename: String, list: CustomList[T], `type`: UserTypeInterface[T]): Unit = {
    val out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))
    try {
      out.writeInt(list.getSize())
      list.toArrayList().asScala.foreach { item => {
        out.writeUTF(`type`.serializeValue(item))
      }}
    } finally {
      out.close()
    }
  }

  @throws[IOException]
  override def load[T](filename: String, list: CustomList[T], `type`: UserTypeInterface[T]): Unit = {
    val loaded = new CustomList[T](list.getBlockCapacity())
    val in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)))
    try {
      val count = in.readInt()
      if (count < 0) {
        throw new IOException("Отрицательное количество элементов")
      }
      for (i <- 0 until count) {
        loaded.add(`type`.deserializeValue(in.readUTF()))
      }
      if (in.read() != -1) {
        throw new IOException("Лишние данные после списка")
      }
    } catch {
      case e: IllegalArgumentException => {
        throw new IOException("Значение не соответствует выбранному типу", e)
      }
    } finally {
      in.close()
    }

    list.clear()
    loaded.forEach { item => {
      list.add(item)
    }}
  }
}
