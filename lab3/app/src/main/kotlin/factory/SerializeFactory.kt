package factory

import inface.SerializeStrategyInterface
import serialize.BinarySerializeStrategy
import serialize.JsonSerializeStrategy
import serialize.PlainTxtSerializeStrategy

// Фабрика стратегий сериализации
object SerializeFactory {
    
    private val strategies: List<SerializeStrategyInterface> = listOf(
        PlainTxtSerializeStrategy(),
        BinarySerializeStrategy(),
        JsonSerializeStrategy()
    )

    // Вычисляемое свойство возвращает список имен форматов
    val formatNameList: List<String>
        get() = strategies.map { it.formatName }

    @JvmStatic
    fun getStrategyByName(name: String): SerializeStrategyInterface {
        // Находим стратегию по имени или возвращаем дефолтную (первую)
        return strategies.find { it.formatName == name } ?: strategies[0]
    }
}
