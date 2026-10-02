package factory

import inface.SortStrategyInterface
import sort.MergeSortStrategy
import sort.QuickSortStrategy

// Фабрика стратегий сортировок
object SortFactory {
    
    private val strategies: List<SortStrategyInterface> = listOf(
        QuickSortStrategy(),
        MergeSortStrategy()
    )

    val strategyNameList: List<String>
        get() = strategies.map { it.strategyName }

    @JvmStatic
    fun getStrategyByName(name: String): SortStrategyInterface {
        return strategies.find { it.strategyName == name } ?: strategies[0]
    }
}
