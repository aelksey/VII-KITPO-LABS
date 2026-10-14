package factory

import inface.TraverseStrategyInterface
import traverse.LinearTraverseStrategy
import traverse.ReverseTraverseStrategy

// Фабрика для получения стратегий обхода
object TraverseFactory {

    val strategyNameList: List<String> = listOf("Линейный обход", "Обратный обход")

    @JvmStatic
    fun <T> getStrategyByName(name: String?): TraverseStrategyInterface<T>? {
        if (name == null) return null
        
        return when (name.trim()) {
            "Линейный обход" -> LinearTraverseStrategy()
            "Обратный обход" -> ReverseTraverseStrategy()
            else -> null
        }
    }
}
