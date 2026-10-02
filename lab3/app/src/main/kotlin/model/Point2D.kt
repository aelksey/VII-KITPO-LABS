package model

import kotlin.math.sqrt

// Использование data class заменяет рутинный код Java
data class Point2D(
    val x: Double = 0.0,
    val y: Double = 0.0
) {
    // Вычисляемое свойство (Property) вместо метода getDistance()
    val distance: Double
        get() = sqrt(x * x + y * y)
}
