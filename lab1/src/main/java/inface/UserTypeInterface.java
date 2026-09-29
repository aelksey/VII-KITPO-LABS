package inface;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

// Интерфейс хранимого типа данных
public interface UserTypeInterface<T> {
    default java.util.Comparator<T> getTypeComparator() { return this::compare; }

    // Примеры принадлежат типу
    default java.util.List<T> sampleValues() { return java.util.Collections.singletonList(create()); }

    // Представление для файла может отличаться от подписи в интерфейсе
    default String serializeValue(T object) throws IOException {
        java.io.StringWriter buffer = new java.io.StringWriter();
        try (BufferedWriter writer = new BufferedWriter(buffer)) { writeValue(object, writer); }
        return buffer.toString();
    }

    default T deserializeValue(String value) { return parseValue(value); }
    String typeName();                           // Имя типа для GUI
    T create();                                  // Создать пустой объект
    T randomValue(java.util.random.RandomGenerator random); // Создать случайное значение
    T clone(T object);                           // Клонировать объект
    T readValue(InputStreamReader in);          // Читать из потока
    T parseValue(String ss);                     // Парсить из строки
    
    // Встроенный компаратор для сортировки
    int compare(T o1, T o2);                     
    
    // Метод для красивого вывода в GUI
    String toString(T object);                   
    
    // Сериализация конкретного объекта в строку файла
    void writeValue(T object, BufferedWriter writer) throws IOException; 
}
