import core.CustomList
import factory.SerializeFactory
import factory.SortFactory
import factory.UserFactory
import inface.ForEachCallbackInterface
import inface.UserTypeInterface
import java.nio.file.Files
import java.nio.file.Path

// Один сценарий для каждого прототипа из фабрики
object MainTest {
    
    @JvmStatic
    fun main(args: Array<String>) {
        val directory = Path.of("target", "demo")
        Files.createDirectories(directory)
        
        var index = 0
        // Использование свойств фабрики вместо get-методов
        for (name in UserFactory.typeNameList) {
            demonstrate(UserFactory.getBuilderByName(name), directory.resolve("type-\${index++}"))
        }
        println("Все типы: демонстрация завершена успешно")
    }

    private fun <T> demonstrate(type: UserTypeInterface<T>, prefix: Path) {
        println("\nТип: \${type.typeName}")
        
        val list = CustomList<T>(2)
        
        // Цикл по свойству sampleValues, сериализация/десериализация цепочкой
        for (sample in type.sampleValues) {
            list.add(type.clone(type.parseValue(type.serializeValue(sample))))
        }
        
        val first = list.get(0)
        list.insert(1, type.clone(first))
        list.remove(1)
        
        // Обращение к свойствам структуры данных вместо геттеров
        println("Узлов-массивов: \${list.nodeCount}, ячеек в узле: \${list.blockCapacity}")
        
        // SAM-конверсия для интерфейса ForEachCallbackInterface
        list.forEach(ForEachCallbackInterface { value -> 
            println("  \${type.toString(value)}") 
        })
        
        // Поиск первого подходящего элемента через предикат
        println("firstThat: \${type.toString(list.firstThat { v -> type.compare(v, first) == 0 })}")
        
        for (sort in SortFactory.strategyNameList) {
            list.sort(SortFactory.getStrategyByName(sort), type.typeComparator)
            println(sort)
            // Идиоматичная передача лямбды за круглые скобки
            list.forEach { v -> println("  \${type.toString(v)}") }
        }
        
        var formatIndex = 0
        for (format in SerializeFactory.formatNameList) {
            val serializer = SerializeFactory.getStrategyByName(format)
            val filename = "\$prefix-format-\${formatIndex++}.data"
            
            serializer.save(filename, list, type)
            
            val restored = CustomList<T>()
            restored.add(type.clone(first)) // Загрузка должна заменить старые данные
            serializer.load(filename, restored, type)
            
            if (list.size != restored.size) {
                throw IllegalStateException("Размер после загрузки")
            }
            
            for (i in 0 until list.size) {
                if (type.serializeValue(list.get(i)) != type.serializeValue(restored.get(i))) {
                    throw IllegalStateException("Данные после загрузки: \$format")
                }
            }
            println("Сохранение/загрузка OK: \$format")
        }
        
        list.clear()
        println("clear: размер = \${list.size}")
    }
}
