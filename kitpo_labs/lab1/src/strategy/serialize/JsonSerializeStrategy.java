package serialize;

import core.CustomList;
import inface.SerializeStrategyInterface;
import inface.UserTypeInterface;

import java.io.*;

/**
 * Стратегия сериализации данных в формат JSON (Plain Text).
 */
public class JsonSerializeStrategy implements SerializeStrategyInterface {

    @Override
    public String formatName() {
        // Возвращаем имя формата для выпадающего списка в GUI
        return "(JSON)";
    }

    @Override
    public <T> void save(String filename, CustomList<T> list, UserTypeInterface<T> userType) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("[\n");

        int size = list.getSize();
        for (int i = 0; i < size; i++) {
            T item = list.get(i);
            // Превращаем объект в строковое представление через переданный userType
            String valueStr = userType.toString(item);
            
            // Экранируем кавычки для валидного JSON
            String escapedValue = valueStr.replace("\"", "\\\"");
            
            json.append("  \"").append(escapedValue).append("\"");
            if (i < size - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]");

        // Записываем сформированную JSON-строку в файл
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(json.toString());
        }
    }

    @Override
    public <T> void load(String filename, CustomList<T> list, UserTypeInterface<T> userType) throws IOException {
        list.clear(); // Перед загрузкой очищаем список
        
        File file = new File(filename);
        if (!file.exists()) {
            return; // Если файла нет, оставляем список пустым
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Пропускаем служебные JSON символы: [, ], или пустые строки
                if (line.equals("[") || line.equals("]") || line.isEmpty()) {
                    continue;
                }

                // Убираем запятую в конце строки, если она есть
                if (line.endsWith(",")) {
                    line = line.substring(0, line.length() - 1).trim();
                }

                // Извлекаем значение между JSON-кавычками: "значение"
                if (line.startsWith("\"") && line.endsWith("\"")) {
                    String cleanValue = line.substring(1, line.length() - 1);
                    // Возвращаем экранированные кавычки обратно
                    cleanValue = cleanValue.replace("\\\"", "\"");
                    
                    // Парсим значение через userType и добавляем в список
                    try {
                        T item = userType.parseValue(cleanValue);
                        list.add(item);
                    } catch (Exception e) {
                        // Переворачиваем общее исключение парсинга в IOException, чтобы соответствовать сигнатуре
                        throw new IOException("Ошибка парсинга значения '" + cleanValue + "': " + e.getMessage(), e);
                    }
                }
            }
        }
    }
}