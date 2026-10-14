package nstu.rgr.serialize;

import nstu.rgr.core.CustomList;
import nstu.rgr.inface.SerializeStrategyInterface;
import nstu.rgr.inface.UserTypeInterface;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Collectors;

public class JsonSerializeStrategy implements SerializeStrategyInterface {
    @Override
    public String fileExtension() { return "json"; }

    @Override
    public String formatName() { return "JSON"; }

    @Override
    public <T> void save(String filename, CustomList<T> list, UserTypeInterface<T> type) throws IOException {
        try {
            // Заменяем method reference (type::serializeValue) на явную лямбду с try-catch
            String jsonContent = list.toArrayList().stream()
                    .map(item -> {
                        try {
                            return type.serializeValue(item);
                        } catch (Exception e) {
                            // Оборачиваем checked IOException в unchecked RuntimeException
                            throw new RuntimeException(e);
                        }
                    })
                    .map(JsonStrings::quote)
                    .collect(Collectors.joining(",\n", "[\n", "\n]\n"));

            Files.write(Path.of(filename), Collections.singletonList(jsonContent), StandardCharsets.UTF_8);

        } catch (RuntimeException e) {
            // Если внутри стрима вылетел RuntimeException, распаковываем его обратно в IOException
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw new IOException("Ошибка сериализации данных: " + e.getMessage(), e);
        }
    }


    @Override
    public <T> void load(String filename, CustomList<T> list, UserTypeInterface<T> type) throws IOException {
        Path path = Path.of(filename);
        if (Files.notExists(path)) {
            list.clear();
            return;
        }

        try {
            byte[] bytes = Files.readAllBytes(path);
            String fileContent = new String(bytes, StandardCharsets.UTF_8);

            CustomList<T> loaded = new CustomList<>(list.getBlockCapacity());
            JsonStrings.parse(fileContent).stream()
                    .map(type::deserializeValue)
                    .forEach(loaded::add);

            list.clear();
            loaded.forEach(list::add);

        } catch (IllegalArgumentException e) {
            throw new IOException("Значение не соответствует выбранному типу", e);
        } catch (IOException e) {
            throw new IOException("Ошибка при чтении JSON файла: " + e.getMessage(), e);
        }
    }
}
