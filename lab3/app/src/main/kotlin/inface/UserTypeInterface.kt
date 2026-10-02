package inface

import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringWriter
import java.util.Collections
import java.util.random.RandomGenerator

// Интерфейс хранимого типа данных
interface UserTypeInterface<T> {

    val typeComparator: Comparator<T>
        get() = Comparator { o1, o2 -> compare(o1, o2) }

    // Примеры принадлежат типу
    val sampleValues: List<T>
        get() = Collections.singletonList(create())

    val typeName: String // Имя типа для GUI

    // Представление для файла может отличаться от подписи в интерфейсе
    @Throws(IOException::class)
    fun serializeValue(`object`: T): String {
        val buffer = StringWriter()
        BufferedWriter(buffer).use { writer -> 
            writeValue(`object`, writer) 
        }
        return buffer.toString()
    }

    fun deserializeValue(value: String): T = parseValue(value)

    fun create(): T                                         // Создать пустой объект
    fun randomValue(random: RandomGenerator): T             // Создать случайное значение
    fun clone(`object`: T): T                               // Клонировать объект
    fun readValue(`in`: InputStreamReader): T               // Читать из потока
    fun parseValue(ss: String): T                           // Парсить из строки
    
    // Встроенный компаратор для сортировки
    fun compare(o1: T, o2: T): Int                     
    
    // Метод для красивого вывода в GUI
    fun toString(`object`: T): String                   
    
    // Сериализация конкретного объекта в строку файла
    @Throws(IOException::class)
    fun writeValue(`object`: T, writer: BufferedWriter) 
}
