package inface

import java.io.{BufferedWriter, IOException, InputStreamReader, StringWriter}
import java.util.Collections
import java.util.random.RandomGenerator
import java.util.Comparator

// Интерфейс хранимого типа данных
trait UserTypeInterface[T] {
  
  // Возвращает Java Comparator на основе метода compare этой же структуры
  def getTypeComparator(): Comparator[T] = {(o1: T, o2: T) => UserTypeInterface.this.compare(o1, o2)}

  // Примеры принадлежат типу
  def sampleValues(): java.util.List[T] = 
    Collections.singletonList(create())

  // Представление для файла (сериализация конкретного объекта в строку)
  @throws[IOException]
  def serializeValue(`object`: T): String = {
    val buffer = new StringWriter()
    val writer = new BufferedWriter(buffer)
    try 
      writeValue(`object`, writer)
    finally 
      writer.close()
    buffer.toString
  }

  def deserializeValue(value: String): T = parseValue(value)

  def typeName(): String                           // Имя типа для GUI
  def create(): T                                  // Создать пустой объект
  def randomValue(random: RandomGenerator): T      // Создать случайное значение
  def clone(`object`: T): T                        // Клонировать объект
  def readValue(in: InputStreamReader): T          // Читать из потока
  def parseValue(ss: String): T                     // Парсить из строки
  
  def compare(o1: T, o2: T): Int                   // Встроенный компаратор для сортировки
  def toString(`object`: T): String                // Метод для красивого вывода в GUI
  
  @throws[IOException]
  def writeValue(`object`: T, writer: BufferedWriter): Unit // Сериализация в файл через BufferedWriter
}