package types;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import inface.UserTypeInterface;

public class StringStrategy implements UserTypeInterface<String> {
    @Override
    public String randomValue(java.util.random.RandomGenerator random) {
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int length = random.nextInt(3, 13);
        StringBuilder value = new StringBuilder(length);
        for (int i = 0; i < length; i++) value.append(alphabet.charAt(random.nextInt(alphabet.length())));
        return value.toString();
    }

    @Override
    public java.util.List<String> sampleValues() { return java.util.List.of("Гамма", "Альфа", "Бета", "Дельта", "Омега", "Лямбда"); }

    @Override
    public String deserializeValue(String value) { return value; }
    @Override
    public String typeName() { 
        return "Строка"; 
    }

    @Override
    public String create() { 
        return ""; 
    }

    @Override
    public String clone(String object) { 
        if (object == null) return null;
        // Конструктор гарантирует создание честной независимой копии строки
        return new String(object); 
    }

    @Override
    public String parseValue(String ss) { 
        // Возвращаем строку без лишних пробелов по краям.
        return ss.trim(); 
    }

    @Override
    public String readValue(InputStreamReader in) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(in)) {
            String line = br.readLine();
            if (line == null) return null;
            return parseValue(line);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int compare(String o1, String o2) { 
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;
        return o1.compareTo(o2); 
    }

    @Override
    public String toString(String object) { 
        return object != null ? object : ""; 
    }

    @Override
    public void writeValue(String object, BufferedWriter writer) throws IOException {
        writer.write(toString(object));
    }
}

