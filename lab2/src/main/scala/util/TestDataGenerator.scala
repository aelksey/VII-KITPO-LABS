package util

import core.CustomList
import inface.UserTypeInterface
import java.util.Random

// Генерирует объекты через прототип и упаковывает ссылки в массивы узлов
object TestDataGenerator {
  private val RANDOM: Random = new Random()

  def generateRandomList[T](
    `type`: UserTypeInterface[T],
    maxListSize: Int,
    maxBlockCapacity: Int,
    randomCapacity: Boolean
  ): CustomList[T] = {
    
    if ((maxListSize < 1) || (maxBlockCapacity < 1)) {
      throw new IllegalArgumentException("Размер списка и массива должны быть положительными")
    }
    
    val capacity: Int = if (randomCapacity) {
      RANDOM.nextInt(maxBlockCapacity) + 1
    } else {
      maxBlockCapacity
    }
    
    val list: CustomList[T] = new CustomList[T](capacity)
    val size: Int = RANDOM.nextInt(maxListSize) + 1
    
    for (i <- 0 until size) {
      list.add(`type`.randomValue(RANDOM))
    }
    
    list
  }
}
