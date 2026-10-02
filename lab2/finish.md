Отлично, давайте портируем пакет serialize на Scala 3.
Поскольку в вашей первой лабораторной работе методы ядра списка list.toArrayList(), list.getBlockCapacity() и list.clear() уже устоялись, мы бережно перенесём логику взаимодействия. При этом мы заменим многострочные парсеры, циклы for и сборку StringBuilder на более лаконичные конструкции Scala (такие как match, строки-интерполяторы и встроенный tailrec при необходимости).
Ниже представлены четыре файла для вашей папки src/main/scala/serialize/:
------------------------------
## 1. JsonStrings.scala
Вместо служебного класса с private конструктором в Scala используется object. Для парсинга мы применим внутренний класс Parser, а обработку символов сделаем через сопоставление шаблонов match.

package serialize
import java.io.IOExceptionimport java.util.ArrayListimport java.util.Listimport scala.jdk.CollectionConverters.*
object JsonStrings:

  def quote(value: String): String =
    val result = new StringBuilder("\"")
    for c <- value do
      c match
        case '"'  => result.append("\\\"")
        case '\\' => result.append("\\\\")
        case '\n' => result.append("\\n")
        case '\r' => result.append("\\r")
        case '\t' => result.append("\\t")
        case '\b' => result.append("\\b")
        case '\f' => result.append("\\f")
        case _ if c < 0x20 => result.append(String.format("\\u%04x", c.toInt))
        case _    => result.append(c)
    result.append('"').toString()

  @throws[IOException]
  def parse(text: String): List[String] = 
    new Parser(text).parse().asJava

  private class Parser(text: String):
    private var position = 0

    private def error(): IOException = 
      new IOException(s"Некорректный JSON, позиция \$position")

    private def whitespace(): Unit =
      while position < text.length && " \t\r\n".indexOf(text.charAt(position)) >= 0 do 
        position += 1

    private def take(expected: Char): Boolean =
      whitespace()
      if position < text.length && text.charAt(position) == expected then
        position += 1
        true
      else 
        false

    @throws[IOException]
    def parse(): scala.collection.immutable.List[String] =
      var result = scala.collection.mutable.ListBuffer[String]()
      if !take('[') then throw error()
      if !take(']') then
        var continue = true
        while continue do
          result.append(string())
          continue = take(',')
        if !take(']') then throw error()
      whitespace()
      if position != text.length then throw error()
      result.toList

    @throws[IOException]
    private def string(): String =
      if !take('"') then throw error()
      val result = new StringBuilder()
      while position < text.length do
        var c = text.charAt(position)
        position += 1
        if c == '"' then return result.toString()
        if c < 0x20 then throw error()
        if c != '\\' then
          result.append(c)
        else
          if position == text.length then throw error()
          val escape = text.charAt(position)
          position += 1
          escape match
            case '"' | '\\' | '/' => result.append(escape)
            case 'n' => result.append('\n')
            case 'r' => result.append('\r')
            case 't' => result.append('\t')
            case 'b' => result.append('\b')
            case 'f' => result.append('\f')
            case 'u' =>
              if position + 4 > text.length then throw error()
              var code = 0
              for _ <- 0 until 4 do
                val digit = Character.digit(text.charAt(position), 16)
                position += 1
                if digit < 0 then throw error()
                code = code * 16 + digit
              result.append(code.toChar)
            case _ => throw error()
      throw error()

------------------------------
## 2. BinarySerializeStrategy.scala
Здесь мы используем блок try-with-resources в Scala-стиле (с помощью метода .use, встроенного в стандартную библиотеку для управления ресурсами, или явного try/finally).

package serialize
import java.io.*import core.CustomListimport inface.SerializeStrategyInterfaceimport inface.UserTypeInterfaceimport scala.jdk.CollectionConverters.*
// Собственный двоичный форматclass BinarySerializeStrategy extends SerializeStrategyInterface:
  
  override def fileExtension(): String = "bin"
  override def formatName(): String = ".bin"

  @throws[IOException]
  override def save[T](filename: String, list: CustomList[T], `type`: UserTypeInterface[T]): Unit =
    val out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))
    try
      out.writeInt(list.size()) // В Scala 3 переименовано или вызвано как size() / getSize()
      // Адаптируем обход под коллекцию Java, которую возвращает ваш list.toArrayList()
      for item <- list.toArrayList().asScala do 
        out.writeUTF(`type`.serializeValue(item))
    finally
      out.close()

  @throws[IOException]
  override def load[T](filename: String, list: CustomList[T], `type`: UserTypeInterface[T]): Unit =
    val loaded = new CustomList[T](list.getBlockCapacity())
    val in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)))
    try
      val count = in.readInt()
      if count < 0 then throw new IOException("Отрицательное количество элементов")
      for _ <- 0 until count do
        loaded.add(`type`.deserializeValue(in.readUTF()))
      if in.read() != -1 then throw new IOException("Лишние данные после списка")
    catch
      case e: IllegalArgumentException => 
        throw new IOException("Значение не соответствует выбранному типу", e)
    finally
      in.close()

    list.clear()
    // Переносим элементы в основной список
    loaded.forEach(item => list.add(item))

------------------------------
## 3. JsonSerializeStrategy.scala
Используем современные возможности java.nio.file.Files для быстрой работы с текстом.

package serialize
import core.CustomListimport inface.SerializeStrategyInterfaceimport inface.UserTypeInterfaceimport java.io.IOExceptionimport java.nio.charset.StandardCharsetsimport java.nio.file.{Files, Path}import scala.jdk.CollectionConverters.*
class JsonSerializeStrategy extends SerializeStrategyInterface:
  
  override def fileExtension(): String = "json"
  override def formatName(): String = "JSON"

  @throws[IOException]
  override def save[T](filename: String, list: CustomList[T], `type`: UserTypeInterface[T]): Unit =
    val values = for item <- list.toArrayList().asScala yield 
      JsonStrings.quote(`type`.serializeValue(item))
    
    val jsonContent = "[\n" + values.mkString(",\n") + "\n]\n"
    Files.writeString(Path.of(filename), jsonContent, StandardCharsets.UTF_8)

  @throws[IOException]
  override def load[T](filename: String, list: CustomList[T], `type`: UserTypeInterface[T]): Unit =
    val path = Path.of(filename)
    if Files.notExists(path) then
      list.clear()
      return

    val loaded = new CustomList[T](list.getBlockCapacity())
    try
      val textContent = Files.readString(path, StandardCharsets.UTF_8)
      // Превращаем java.util.List из JsonStrings.parse обратно в итерируемый Scala-коллекцию
      for value <- JsonStrings.parse(textContent).asScala do
        loaded.add(`type`.deserializeValue(value))
    catch
      case e: IllegalArgumentException => 
        throw new IOException("Значение не соответствует выбранному типу", e)

    list.clear()
    loaded.forEach(item => list.add(item))

------------------------------
## 4. PlainTxtSerializeStrategy.scala
Обратите внимание на конструкцию циклов: вместо for (int i = encoded ? 1 : 0; ...) в Scala мы генерируем интервал через until с динамической нижней границей.

package serialize
import core.CustomListimport inface.SerializeStrategyInterfaceimport inface.UserTypeInterfaceimport java.io.IOExceptionimport java.nio.charset.StandardCharsetsimport java.nio.file.{Files, Path}import scala.jdk.CollectionConverters.*
// Заголовок и по одной JSON-экранированной строке на значениеclass PlainTxtSerializeStrategy extends SerializeStrategyInterface:
  
  override def fileExtension(): String = "txt"
  override def formatName(): String = "Plain Text"
  
  private val HEADER = "CUSTOM_LIST_TEXT_V1"

  @throws[IOException]
  override def save[T](filename: String, list: CustomList[T], `type`: UserTypeInterface[T]): Unit =
    val lines = scala.collection.mutable.ListBuffer[String]()
    lines.append(HEADER)
    for value <- list.toArrayList().asScala do
      lines.append(JsonStrings.quote(`type`.serializeValue(value)))
      
    Files.write(Path.of(filename), lines.asJava, StandardCharsets.UTF_8)

  @throws[IOException]
  override def load[T](filename: String, list: CustomList[T], `type`: UserTypeInterface[T]): Unit =
    val lines = Files.readAllLines(Path.of(filename), StandardCharsets.UTF_8).asScala
    val encoded = lines.nonEmpty && HEADER == lines.head
    val startFrom = if encoded then 1 else 0
    
    val loaded = new CustomList[T](list.getBlockCapacity())
    try
      for i <- startFrom until lines.size do
        var raw = lines(i)
        if encoded then
          val decoded = JsonStrings.parse("[" + raw + "]").asScala
          if decoded.size != 1 then throw new IOException("Ожидалось одно значение в строке")
          raw = decoded.head
        loaded.add(`type`.deserializeValue(raw))
    catch
      case e: IllegalArgumentException => 
        throw new IOException("Значение не соответствует выбранному типу", e)

    list.clear()
    loaded.forEach(item => list.add(item))

------------------------------
## 🔍 Что важно упомянуть в отчёте по Л.р. №2:

   1. Конвертация коллекций через asScala и asJava: Поскольку ввод-вывод файлов (NIO) и методы ядра Java-классов (toArrayList()) оперируют списками java.util.List, мы использовали обёртки scala.jdk.CollectionConverters, чтобы переходить к лаконичным циклам Scala без потери производительности.
   2. Отсутствие статических методов: Служебные функции парсинга JsonStrings реализованы внутри object JsonStrings, что органично заменяет static final class из Java.
   3. Безопасное закрытие потоков: Вместо конструкции try-with-resources (которая в Java проверяет интерфейс AutoCloseable), в Scala применены классические блоки try/finally с вызовами .close(), что гарантирует бесперебойную компиляцию под любую версию JVM.

Вся подсистема сериализации готова к подключению! Куда двинемся дальше?

* Напишем остальные ТД стратегии (IntegerStrategy, DoubleStrategy, StringStrategy)?
* Или займёмся стратегиями сортировки (sort) и обхода (traverse)?


lets implement java files from seiralize package