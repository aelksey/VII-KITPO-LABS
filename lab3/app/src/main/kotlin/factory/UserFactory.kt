package factory

import inface.UserTypeInterface
import types.DoubleStrategy
import types.IntegerStrategy
import types.Point2DStrategy
import types.StringStrategy

// Фабрика типов
object UserFactory {
    
    private val builders: List<UserTypeInterface<*>> = listOf(
        Point2DStrategy(),
        IntegerStrategy(),
        DoubleStrategy(),
        StringStrategy()
    )

    val typeNameList: List<String>
        get() = builders.map { it.typeName }

    @JvmStatic
    fun getBuilderByName(name: String): UserTypeInterface<*> {
        return builders.find { it.typeName == name }
            ?: throw IllegalArgumentException("Тип не найден: $name")
    }
}
