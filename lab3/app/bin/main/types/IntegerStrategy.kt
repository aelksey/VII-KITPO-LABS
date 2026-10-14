package types

import inface.UserTypeInterface
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.util.random.RandomGenerator

class IntegerStrategy : UserTypeInterface<Int> {

    override val typeName: String = "Целое число"
    
    override val sampleValues: List<Int> = listOf(42, -7, 15, 0, 100, -25)

    override fun randomValue(random: RandomGenerator): Int {
        return random.nextInt(-1000, 1001)
    }

    override fun create(): Int = 0

    override fun clone(`object`: Int): Int = `object`

    override fun parseValue(ss: String): Int = ss.trim().toInt()

    // Исправлено: тип возвращаемого значения изменен на Int (не-null)
    override fun readValue(`in`: InputStreamReader): Int {
        return try {
            BufferedReader(`in`).use { br ->
                val line = br.readLine() ?: return create() // Если строка пустая, возвращаем дефолт
                parseValue(line)
            }
        } catch (e: Exception) {
            create() // В случае ошибки парсинга или ввода-вывода возвращаем дефолт
        }
    }

    override fun compare(o1: Int, o2: Int): Int = o1.compareTo(o2)

    override fun toString(`object`: Int): String = `object`.toString()

    @Throws(IOException::class)
    override fun writeValue(`object`: Int, writer: BufferedWriter) {
        writer.write(`object`.toString())
    }
}
