package sort

import core.CustomList
import inface.SortStrategyInterface
import java.util.Comparator

class QuickSortStrategy : SortStrategyInterface {
    override val strategyName: String = "Быстрая сортировка"

    override fun <T> sort(list: CustomList<T>, comparator: Comparator<in T>) {
        if (list.size < 2) return
        // countLeadingZeroBits() — встроенный аналог Integer.numberOfLeadingZeros() в Kotlin
        val depth = 2 * (31 - list.size.countLeadingZeroBits())
        quickSort(list, 0, list.size - 1, depth, comparator)
    }

    private fun <T> quickSort(list: CustomList<T>, low: Int, high: Int, depth: Int, c: Comparator<in T>) {
        if (low >= high) return
        if (depth == 0) {
            val part = CustomList<T>(list.blockCapacity)
            for (i in low..high) part.add(list.get(i))
            
            MergeSortStrategy().sort(part, c)
            
            for (i in 0 until part.size) list.set(low + i, part.get(i))
            return
        }
        
        val pivot = list.get(low + (high - low) / 2)
        var i = low
        var j = high
        while (i <= j) {
            while (c.compare(list.get(i), pivot) < 0) i++
            while (c.compare(list.get(j), pivot) > 0) j--
            if (i <= j) {
                list.swap(i++, j--)
            }
        }
        quickSort(list, low, j, depth - 1, c)
        quickSort(list, i, high, depth - 1, c)
    }
}
