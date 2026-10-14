package types;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import inface.UserTypeInterface;

public class IntegerStrategy implements UserTypeInterface<Integer> {
    @Override
    public Integer randomValue(java.util.random.RandomGenerator random) {
        return random.nextInt(-1000, 1001);
    }

    @Override
    public java.util.List<Integer> sampleValues() { return java.util.List.of(42, -7, 15, 0, 100, -25); }
    @Override
    public String typeName() { return "Целое число"; }

    @Override
    public Integer create() { return 0; }

    @Override
    public Integer clone(Integer object) { return Integer.valueOf(object); }

    @Override
    public Integer parseValue(String ss) { return Integer.parseInt(ss.trim()); }

    @Override
    public Integer readValue(InputStreamReader in) {
        // try-with-resources гарантирует закрытие буферизированного потока
        try (java.io.BufferedReader br = new java.io.BufferedReader(in)) {
            String line = br.readLine();
            if (line == null) return null;
            return parseValue(line);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int compare(Integer o1, Integer o2) { return Integer.compare(o1, o2); }

    @Override
    public String toString(Integer object) { return String.valueOf(object); }

    @Override
    public void writeValue(Integer object, BufferedWriter writer) throws IOException {
        writer.write(String.valueOf(object));
    }
}

