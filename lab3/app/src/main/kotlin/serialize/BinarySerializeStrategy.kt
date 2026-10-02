package serialize

import core.CustomList
import inface.SerializeStrategyInterface
import inface.UserTypeInterface
import java.io.*

// Собственный двоичный формат
class BinarySerializeStrategy : SerializeStrategyInterface {
    override val fileExtension: String = "bin"
    override val formatName: String = ".bin"

    @Throws(IOException::class)
    override fun <T> save(filename: String, list: CustomList<T>, userType: UserTypeInterface<T>) {
        DataOutputStream(BufferedOutputStream(FileOutputStream(filename))).use { out ->
            out.writeInt(list.size)
            for (item in list.toArrayList()) {
                out.writeUTF(userType.serializeValue(item))
            }
        }
    }

    @Throws(IOException::class)
    override fun <T> load(filename: String, list: CustomList<T>, userType: UserTypeInterface<T>) {
        val loaded = CustomList<T>(list.blockCapacity)
        try {
            DataInputStream(BufferedInputStream(FileInputStream(filename))).use { `in` ->
                val count = `in`.readInt()
                if (count < 0) throw IOException("Отрицательное количество элементов")
                for (i in 0 until count) {
                    loaded.add(userType.deserializeValue(`in`.readUTF()))
                }
                if (`in`.read() != -1) throw IOException("Лишние данные после списка")
            }
        } catch (e: IllegalArgumentException) {
            throw IOException("Значение не соответствует выбранному типу", e)
        }
        list.clear()
        loaded.forEach(list::add)
    }
}
