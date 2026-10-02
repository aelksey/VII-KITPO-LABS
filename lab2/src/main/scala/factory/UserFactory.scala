package factory

import java.util.ArrayList
import java.util.List
import inface.UserTypeInterface
import types._
import scala.jdk.CollectionConverters._

object UserFactory {
  private val builders: List[UserTypeInterface[_]] = new ArrayList[UserTypeInterface[_]]()

  // Блок инициализации
  {
    builders.add(new Point2DStrategy())
    builders.add(new IntegerStrategy())
    builders.add(new DoubleStrategy())
    builders.add(new StringStrategy())
  }

  def getTypeNameList(): List[String] = {
    val names: List[String] = new ArrayList[String]()
    builders.asScala.foreach { b => {
      names.add(b.typeName())
    }}
    names
  }

  def getBuilderByName(name: String): UserTypeInterface[_] = {
    var result: UserTypeInterface[_] = null
    builders.asScala.foreach { b => {
      if (b.typeName().equals(name)) {
        if (result == null) {
          result = b
        }
      }
    }}
    if (result != null) {
      result
    } else {
      throw new IllegalArgumentException("Тип не найден: " + name)
    }
  }
}
