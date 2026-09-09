package serialize;

import java.io.*;
import core.CustomList;
import inface.SerializeStrategyInterface;
import inface.UserTypeInterface;

// Реализация интерфейса стратегии сериализации (1. Простой текст)
public class PlainTxtSerializeStrategy implements SerializeStrategyInterface {
    @Override
    public String formatName() { return "Текстовый формат (Plain Text )"; }

    @Override
    public <T> void save(String filename, CustomList<T> list, UserTypeInterface<T> userType) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Используем встроенный итератор forEach нашего списка
            list.forEach(item -> {
                try {
                    userType.writeValue(item, writer);
                    writer.newLine();
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
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    T obj = userType.parseValue(line);
                    list.add(obj); // Добавляем восстановленный объект в конец списка
                }
            }
        }
    }
}

