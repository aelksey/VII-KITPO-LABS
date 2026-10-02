package factory

import java.util.ArrayList
import java.util.List
import inface.SortStrategyInterface
import sort._
import scala.jdk.CollectionConverters._

object SortFactory {
  private val strategies: List[SortStrategyInterface] = new ArrayList[SortStrategyInterface]()

  // Блок инициализации
  {
    strategies.add(new QuickSortStrategy())
    strategies.add(new MergeSortStrategy())
  }

  def getStrategyNameList(): List[String] = {
    val names: List[String] = new ArrayList[String]()
    strategies.asScala.foreach { s => {
      names.add(s.strategyName())
    }}
    names
  }

  def getStrategyByName(name: String): SortStrategyInterface = {
    var result: SortStrategyInterface = null
    strategies.asScala.foreach { s => {
      if (s.strategyName().equals(name)) {
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
