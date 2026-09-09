package serialize;

import java.io.*;
import core.CustomList;
import inface.SerializeStrategyInterface;
import inface.UserTypeInterface;

// Реализация интерфейса стратегии сериализации (2.Собственный двоичный формат)
public class BinarySerializeStrategy implements SerializeStrategyInterface {
    @Override
    public String formatName() { return "Собственный двоичный формат (.bin)"; }

    @Override
    public <T> void save(String filename, CustomList<T> list, UserTypeInterface<T> userType) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
            // Сначала пишем количество элементов, чтобы при чтении знать размер списка
            out.writeInt(list.getSize());
            
            list.forEach(item -> {
                try {
                    // Используем временный StringWriter, чтобы получить текстовое представление из UserType
                    StringWriter sw = new StringWriter();
                    try (BufferedWriter bw = new BufferedWriter(sw)) {
                        userType.writeValue(item, bw);
                    }
                    // Записываем строку в бинарный поток как UTF-8 строку переменной длины
                    out.writeUTF(sw.toString());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    @Override
    public <T> void load(String filename, CustomList<T> list, UserTypeInterface<T> userType) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            int count = in.readInt(); // Читаем сохраненное количество элементов
            for (int i = 0; i < count; i++) {
                String rawData = in.readUTF(); // Читаем бинарную строку
                T obj = userType.parseValue(rawData);
                list.add(obj);
            }
        }
    }
}

