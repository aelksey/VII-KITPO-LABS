package util

import core.CustomList
import inface.UserTypeInterface
import java.util.Random

// Генерирует объекты через прототип и упаковывает ссылки в массивы узлов
class TestDataGenerator {
    
    companion object {
        // Возвращаем стандартный java.util.Random, реализующий RandomGenerator
        private val RANDOM = Random()

        @JvmStatic
        fun <T> generateRandomList(
            type: UserTypeInterface<T>,
            maxListSize: Int,
            maxBlockCapacity: Int,
            randomCapacity: Boolean
        ): CustomList<T> {
            if (maxListSize < 1 || maxBlockCapacity < 1) {
                throw IllegalArgumentException("Размер списка и массива должны быть положительными")
            }
            
            val capacity = if (randomCapacity) RANDOM.nextInt(maxBlockCapacity) + 1 else maxBlockCapacity
            val list = CustomList<T>(capacity)
            
            val size = RANDOM.nextInt(maxListSize) + 1
            for (i in 0 until size) {
                list.add(type.randomValue(RANDOM))
            }
            
            return list
        }
    }
}
