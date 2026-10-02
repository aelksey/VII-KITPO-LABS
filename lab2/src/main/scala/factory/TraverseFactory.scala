package factory

import inface.TraverseStrategyInterface
import traverse.LinearTraverseStrategy
import traverse.ReverseTraverseStrategy
import java.util.List

object TraverseFactory {

  def getStrategyNameList(): List[String] = {
    List.of("Линейный обход", "Обратный обход")
  }

  def getStrategyByName[T](name: String): TraverseStrategyInterface[T] = {
    if (name == null) {
      null
    } else {
      name.trim() match {
        case "Линейный обход" => {
          new LinearTraverseStrategy[T]()
        }
        case "Обратный обход" => {
          new ReverseTraverseStrategy[T]()
        }
        case _ => {
          null
        }
      }
    }
  }
}
