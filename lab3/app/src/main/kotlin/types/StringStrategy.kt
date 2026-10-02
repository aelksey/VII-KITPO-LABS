package types

import inface.UserTypeInterface
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.util.random.RandomGenerator

class StringStrategy : UserTypeInterface<String> {

    override val typeName: String = "Строка"
    
    override val sampleValues: List<String> = listOf("Гамма", "Альфа", "Бета", "Дельта", "Омега", "Лямбда")

    override fun randomValue(random: RandomGenerator): String {
        val alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val length = random.nextInt(3, 13)
        return CharArray(length) { alphabet[random.nextInt(alphabet.length)] }.concatToString()
    }

    override fun deserializeValue(value: String): String = value

    override fun create(): String = ""

    // Исправлено: типы аргумента и возвращаемого значения изменены на не-null String
    override fun clone(`object`: String): String = `object`

    override fun parseValue(ss: String): String = ss.trim()

    // Исправлено: возвращаемый тип изменен на не-null String. 
    // Вместо null возвращается пустая строка через метод create()
    override fun readValue(`in`: InputStreamReader): String {
        return try {
            BufferedReader(`in`).use { br ->
                val line = br.readLine() ?: return create()
                parseValue(line)
            }
        } catch (e: Exception) {
            create()
        }
    }

    // Исправлено: типы аргументов приведены к не-null String
    override fun compare(o1: String, o2: String): Int {
        return o1.compareTo(o2)
    }

    // Исправлено: тип аргумента приведен к не-null String
    override fun toString(`object`: String): String = `object`

    @Throws(IOException::class)
    override fun writeValue(`object`: String, writer: BufferedWriter) {
        writer.write(toString(`object`))
    }
}
