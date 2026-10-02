package inface

import java.io.IOException
import core.CustomList

// Интерфейс стратегии сериализации
trait SerializeStrategyInterface {
  def fileExtension(): String // Расширение без точки для файлового диалога
  def formatName(): String    // Имя формата для выпадающего списка в GUI
  
  // Метод сохранения всего списка
  @throws[IOException]
  def save[T](filename: String, list: CustomList[T], userType: UserTypeInterface[T]): Unit
  
  // Метод загрузки данных в список
  @throws[IOException]
  def load[T](filename: String, list: CustomList[T], userType: UserTypeInterface[T]): Unit
}