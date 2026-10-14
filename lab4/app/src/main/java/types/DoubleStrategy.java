package types;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import inface.UserTypeInterface;

public class DoubleStrategy implements UserTypeInterface<Double> {
    @Override
    public Double randomValue(java.util.random.RandomGenerator random) {
        return random.nextInt(-100000, 100001) / 100.0;
    }

    @Override
    public java.util.List<Double> sampleValues() { return java.util.List.of(3.14, -2.5, 0.125, 0.0, 10.75, -8.25); }
    @Override
    public String typeName() { 
        return "Вещественное число"; 
    }

    @Override
    public Double create() { 
        return 0.0; 
    }

    @Override
    public Double clone(Double object) { 
        if (object == null) return null;
        return Double.valueOf(object); 
    }

    @Override
    public Double parseValue(String ss) { 
        return Double.parseDouble(ss.trim()); 
    }

    @Override
    public Double readValue(InputStreamReader in) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(in)) {
            String line = br.readLine();
            if (line == null) return null;
            return parseValue(line);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int compare(Double o1, Double o2) { 
        return Double.compare(o1, o2); 
    }

    @Override
    public String toString(Double object) { 
        return String.valueOf(object); 
    }

    @Override
    public void writeValue(Double object, BufferedWriter writer) throws IOException {
        writer.write(String.valueOf(object));
    }
}

