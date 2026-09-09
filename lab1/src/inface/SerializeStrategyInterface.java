package inface;

import java.io.IOException;
import core.CustomList;


// Интерфейс стратегии сериализации
public interface SerializeStrategyInterface {
    String formatName(); // Имя формата для выпадающего списка в GUI (например, "Текстовый JSON", "Собственный двоичный")
    // Метод сохранения всего списка
    <T> void save(String filename, CustomList<T> list, UserTypeInterface<T> userType) throws IOException;
    // Метод загрузки данных в список
    <T> void load(String filename, CustomList<T> list, UserTypeInterface<T> userType) throws IOException;
}

