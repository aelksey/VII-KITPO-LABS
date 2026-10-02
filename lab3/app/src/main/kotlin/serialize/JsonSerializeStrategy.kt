package serialize

import core.CustomList
import inface.SerializeStrategyInterface
import inface.UserTypeInterface
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList

class JsonSerializeStrategy : SerializeStrategyInterface {
    override val fileExtension: String = "json"
    override val formatName: String = "JSON"

    @Throws(IOException::class)
    override fun <T> save(filename: String, list: CustomList<T>, userType: UserTypeInterface<T>) {
        val values = ArrayList<String>()
        for (item in list.toArrayList()) {
            values.add(JsonStrings.quote(userType.serializeValue(item)))
        }
        val jsonText = "[\n" + values.joinToString(",\n") + "\n]\n"
        Files.writeString(Path.of(filename), jsonText, StandardCharsets.UTF_8)
    }

    @Throws(IOException::class)
    override fun <T> load(filename: String, list: CustomList<T>, userType: UserTypeInterface<T>) {
        val path = Path.of(filename)
        // Совместимость с прежним контрактом JSON-загрузки
        if (Files.notExists(path)) {
            list.clear()
            return
        }
        val loaded = CustomList<T>(list.blockCapacity)
        try {
            val jsonElements = JsonStrings.parse(Files.readString(path, StandardCharsets.UTF_8))
            for (value in jsonElements) {
                loaded.add(userType.deserializeValue(value))
            }
        } catch (e: IllegalArgumentException) {
            throw IOException("Значение не соответствует выбранному типу", e)
        }
        list.clear()
        loaded.forEach(list::add)
    }
}
