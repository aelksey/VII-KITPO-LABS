package sort

import core.CustomList
import inface.SortStrategyInterface
import java.util.ArrayList

class MergeSortStrategy : SortStrategyInterface {
    override val strategyName: String = "Сортировка слиянием"

    override fun <T> sort(list: CustomList<T>, comparator: Comparator<in T>) {
        if (list.size <= 1) return
        mergeSort(list, 0, list.size - 1, comparator)
    }

    private fun <T> mergeSort(list: CustomList<T>, l: Int, r: Int, comparator: Comparator<in T>) {
        if (l < r) {
            val m = l + (r - l) / 2
            mergeSort(list, l, m, comparator)
            mergeSort(list, m + 1, r, comparator)
            merge(list, l, m, r, comparator)
        }
    }

    private fun <T> merge(list: CustomList<T>, l: Int, m: Int, r: Int, comparator: Comparator<in T>) {
        val left = ArrayList<T>(m - l + 1)
        val right = ArrayList<T>(r - m)

        for (i in l..m) left.add(list.get(i))
        for (j in m + 1..r) right.add(list.get(j))

        var i = 0
        var j = 0
        var k = l
        while (i < left.size && j < right.size) {
            if (comparator.compare(left[i], right[j]) <= 0) {
                list.set(k++, left[i++])
            } else {
                list.set(k++, right[j++])
            }
        }
        while (i < left.size) list.set(k++, left[i++])
        while (j < right.size) list.set(k++, right[j++])
    }
}
