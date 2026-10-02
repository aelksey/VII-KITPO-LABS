package factory

import java.util.ArrayList
import java.util.List
import inface.SerializeStrategyInterface
import serialize._
import scala.jdk.CollectionConverters._

object SerializeFactory {
  private val strategies: List[SerializeStrategyInterface] = new ArrayList[SerializeStrategyInterface]()

  // Блок инициализации
  {
    strategies.add(new PlainTxtSerializeStrategy())
    strategies.add(new BinarySerializeStrategy())
    strategies.add(new JsonSerializeStrategy())
  }

  def getFormatNameList(): List[String] = {
    val names: List[String] = new ArrayList[String]()
    strategies.asScala.foreach { s => {
      names.add(s.formatName())
    }}
    names
  }

  def getStrategyByName(name: String): SerializeStrategyInterface = {
    var result: SerializeStrategyInterface = null
    strategies.asScala.foreach { s => {
      if (s.formatName().equals(name)) {
        if (result == null) {
          result = s
        }
      }
    }}
    if (result != null) {
      result
    } else {
      strategies.get(0)
    }
  }
}
