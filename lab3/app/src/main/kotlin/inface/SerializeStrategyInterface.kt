package inface

import core.CustomList
import java.io.IOException

// Интерфейс стратегии сериализации
interface SerializeStrategyInterface {
    val fileExtension: String // Расширение без точки для файлового диалога
    val formatName: String    // Имя формата для выпадающего списка в GUI

    // Метод сохранения всего списка
    @Throws(IOException::class)
    fun <T> save(filename: String, list: CustomList<T>, userType: UserTypeInterface<T>)

    // Метод загрузки данных в список
    @Throws(IOException::class)
    fun <T> load(filename: String, list: CustomList<T>, userType: UserTypeInterface<T>)
}
