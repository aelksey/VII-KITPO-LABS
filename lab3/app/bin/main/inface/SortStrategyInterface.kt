package inface

import core.CustomList

interface SortStrategyInterface {
    val strategyName: String

    fun <T> sort(list: CustomList<T>, comparator: Comparator<in T>)

    fun <T> sort(list: CustomList<T>, type: UserTypeInterface<T>) {
        sort(list, type.typeComparator)
    }
}
