package serialize;

import java.io.*;
import core.CustomList;
import inface.SerializeStrategyInterface;
import inface.UserTypeInterface;

// Собственный двоичный формат
public class BinarySerializeStrategy implements SerializeStrategyInterface {
    @Override public String fileExtension() { return "bin"; }
    public String formatName() { return ".bin"; }

    public <T> void save(String filename, CustomList<T> list, UserTypeInterface<T> type) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
            out.writeInt(list.getSize());
            for (T item : list.toArrayList()) out.writeUTF(type.serializeValue(item));
        }
    }

    public <T> void load(String filename, CustomList<T> list, UserTypeInterface<T> type) throws IOException {
        CustomList<T> loaded = new CustomList<>(list.getBlockCapacity());
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            int count = in.readInt();
            if (count < 0) throw new IOException("Отрицательное количество элементов");
            for (int i = 0; i < count; i++) loaded.add(type.deserializeValue(in.readUTF()));
            if (in.read() != -1) throw new IOException("Лишние данные после списка");
        } catch (IllegalArgumentException e) { throw new IOException("Значение не соответствует выбранному типу", e); }
        list.clear();
        loaded.forEach(list::add);
    }
}

