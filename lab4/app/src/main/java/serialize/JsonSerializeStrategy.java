package serialize;

import core.CustomList;
import inface.SerializeStrategyInterface;
import inface.UserTypeInterface;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class JsonSerializeStrategy implements SerializeStrategyInterface {
    @Override public String fileExtension() { return "json"; }
    public String formatName() { return "JSON"; }

    public <T> void save(String filename, CustomList<T> list, UserTypeInterface<T> type) throws IOException {
        ArrayList<String> values = new ArrayList<>();
        for (T item : list.toArrayList()) values.add(JsonStrings.quote(type.serializeValue(item)));
        Files.writeString(Path.of(filename), "[\n" + String.join(",\n", values) + "\n]\n", StandardCharsets.UTF_8);
    }

    public <T> void load(String filename, CustomList<T> list, UserTypeInterface<T> type) throws IOException {
        Path path = Path.of(filename);
        // Совместимость с прежним контрактом JSON-загрузки.
        if (Files.notExists(path)) { list.clear(); return; }
        CustomList<T> loaded = new CustomList<>(list.getBlockCapacity());
        try {
            for (String value : JsonStrings.parse(Files.readString(path, StandardCharsets.UTF_8)))
                loaded.add(type.deserializeValue(value));
        } catch (IllegalArgumentException e) { throw new IOException("Значение не соответствует выбранному типу", e); }
        list.clear();
        loaded.forEach(list::add);
    }
}

