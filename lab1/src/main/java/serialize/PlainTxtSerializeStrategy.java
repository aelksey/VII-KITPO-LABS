package serialize;

import core.CustomList;
import inface.SerializeStrategyInterface;
import inface.UserTypeInterface;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Заголовок и по одной JSON-экранированной строке на значение
public class PlainTxtSerializeStrategy implements SerializeStrategyInterface {
    @Override public String fileExtension() { return "txt"; }
    private static final String HEADER = "CUSTOM_LIST_TEXT_V1";
    public String formatName() { return "Plain Text"; }

    public <T> void save(String filename, CustomList<T> list, UserTypeInterface<T> type) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (T value : list.toArrayList()) lines.add(JsonStrings.quote(type.serializeValue(value)));
        Files.write(Path.of(filename), lines, StandardCharsets.UTF_8);
    }

    public <T> void load(String filename, CustomList<T> list, UserTypeInterface<T> type) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(filename), StandardCharsets.UTF_8);
        boolean encoded = !lines.isEmpty() && HEADER.equals(lines.get(0));
        CustomList<T> loaded = new CustomList<>(list.getBlockCapacity());
        try {
            for (int i = encoded ? 1 : 0; i < lines.size(); i++) {
                String raw = lines.get(i);
                if (encoded) {
                    List<String> decoded = JsonStrings.parse("[" + raw + "]");
                    if (decoded.size() != 1) throw new IOException("Ожидалось одно значение в строке");
                    raw = decoded.get(0);
                }
                loaded.add(type.deserializeValue(raw));
            }
        } catch (IllegalArgumentException e) { throw new IOException("Значение не соответствует выбранному типу", e); }
        list.clear();
        loaded.forEach(list::add);
    }
}

