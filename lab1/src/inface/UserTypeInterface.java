package inface;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

// Интерфейс хранимого типа данных
public interface UserTypeInterface<T> {
    String typeName();                           // Имя типа для GUI
    T create();                                  // Создать пустой объект
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

