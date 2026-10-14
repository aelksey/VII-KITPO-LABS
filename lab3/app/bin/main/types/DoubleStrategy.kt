package types

import inface.UserTypeInterface
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.util.random.RandomGenerator

class DoubleStrategy : UserTypeInterface<Double> {
    
    override val typeName: String = "Вещественное число"
    
    override val sampleValues: List<Double> = listOf(3.14, -2.5, 0.125, 0.0, 10.75, -8.25)

    override fun randomValue(random: RandomGenerator): Double {
        return random.nextInt(-100000, 100001) / 100.0
    }

    override fun create(): Double = 0.0

    // Исправлено: типы аргумента и возвращаемого значения изменены на не-null Double
    override fun clone(`object`: Double): Double = `object`

    override fun parseValue(ss: String): Double = ss.trim().toDouble()

    // Исправлено: возвращаемый тип изменен на не-null Double.
    // В случае ошибки парсинга или пустого потока возвращается дефолтное значение через create()
    override fun readValue(`in`: InputStreamReader): Double {
        return try {
            BufferedReader(`in`).use { br ->
                val line = br.readLine() ?: return create()
                parseValue(line)
            }
        } catch (e: Exception) {
            create()
        }
    }

    override fun compare(o1: Double, o2: Double): Int = o1.compareTo(o2)

    override fun toString(`object`: Double): String = `object`.toString()

    @Throws(IOException::class)
    override fun writeValue(`object`: Double, writer: BufferedWriter) {
        writer.write(`object`.toString())
    }
}
