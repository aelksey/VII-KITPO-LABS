package serialize

import core.CustomList
import inface.SerializeStrategyInterface
import inface.UserTypeInterface
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList

// Заголовок и по одной JSON-экранированной строке на значение
class PlainTxtSerializeStrategy : SerializeStrategyInterface {
    override val fileExtension: String = "txt"
    override val formatName: String = "Plain Text"

    companion object {
        private const val HEADER = "CUSTOM_LIST_TEXT_V1"
    }

    @Throws(IOException::class)
    override fun <T> save(filename: String, list: CustomList<T>, userType: UserTypeInterface<T>) {
        val lines = ArrayList<String>()
        lines.add(HEADER)
        for (value in list.toArrayList()) {
            lines.add(JsonStrings.quote(userType.serializeValue(value)))
        }
        Files.write(Path.of(filename), lines, StandardCharsets.UTF_8)
    }

    @Throws(IOException::class)
    override fun <T> load(filename: String, list: CustomList<T>, userType: UserTypeInterface<T>) {
        val lines = Files.readAllLines(Path.of(filename), StandardCharsets.UTF_8)
        val encoded = lines.isNotEmpty() && HEADER == lines[0]
        val loaded = CustomList<T>(list.blockCapacity)
        try {
            val startIndex = if (encoded) 1 else 0
            for (i in startIndex until lines.size) {
                var raw = lines[i]
                if (encoded) {
                    val decoded = JsonStrings.parse("[$raw]")
                    if (decoded.size != 1) throw IOException("Ожидалось одно значение в строке")
                    raw = decoded[0]
                }
                loaded.add(userType.deserializeValue(raw))
            }
        } catch (e: IllegalArgumentException) {
            throw IOException("Значение не соответствует выбранному типу", e)
        }
        list.clear()
        loaded.forEach(list::add)
    }
}
