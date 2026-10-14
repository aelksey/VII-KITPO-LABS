package types

import inface.UserTypeInterface
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.util.random.RandomGenerator
import model.Point2D

// Реализация интерфейса UserType для типа Point2D
class Point2DStrategy : UserTypeInterface<Point2D> {

    override val typeName: String = "2D-точка"
    
    override val sampleValues: List<Point2D> = listOf(
        Point2D(3.0, 4.0), Point2D(1.0, 1.0), Point2D(0.0, 2.0),
        Point2D(5.0, 12.0), Point2D(-3.0, 4.0), Point2D(0.0, 0.0)
    )

    override fun randomValue(random: RandomGenerator): Point2D {
        return Point2D(
            random.nextInt(-10000, 10001) / 100.0,
            random.nextInt(-10000, 10001) / 100.0
        )
    }

    override fun create(): Point2D = Point2D()

    override fun clone(`object`: Point2D): Point2D = `object`.copy()

    override fun parseValue(ss: String): Point2D {
        val parts = ss.trim().split("\\s+".toRegex())
        return Point2D(parts[0].toDouble(), parts[1].toDouble())
    }

    // Исправлено: возвращаемый тип изменен на Point2D (не-null)
    override fun readValue(`in`: InputStreamReader): Point2D {
        return try {
            BufferedReader(`in`).use { br ->
                val line = br.readLine() ?: return create() // Если поток пуст, возвращаем дефолтную точку
                parseValue(line)
            }
        } catch (e: Exception) {
            create() // В случае ошибки парсинга или чтения возвращаем дефолтную точку
        }
    }

    override fun compare(o1: Point2D, o2: Point2D): Int {
        return o1.distance.compareTo(o2.distance)
    }

    override fun toString(`object`: Point2D): String {
        return String.format("Точка(%.2f; %.2f) [Дист: %.2f]", `object`.x, `object`.y, `object`.distance)
    }

    @Throws(IOException::class)
    override fun writeValue(`object`: Point2D, writer: BufferedWriter) {
        writer.write("${`object`.x} ${`object`.y}")
    }
}
