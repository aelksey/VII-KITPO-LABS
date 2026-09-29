package serialize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//JSON-массив строк с представлениями значений ТД
final class JsonStrings {
    private JsonStrings() { }

    static String quote(String value) {
        StringBuilder result = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"' -> result.append("\\\"");
                case '\\' -> result.append("\\\\");
                case '\n' -> result.append("\\n");
                case '\r' -> result.append("\\r");
                case '\t' -> result.append("\\t");
                case '\b' -> result.append("\\b");
                case '\f' -> result.append("\\f");
                default -> {
                    if (c < 0x20) result.append(String.format("\\u%04x", (int)c));
                    else result.append(c);
                }
            }
        }
        return result.append('"').toString();
    }

    static List<String> parse(String text) throws IOException { return new Parser(text).parse(); }

    private static final class Parser {
        private final String text;
        private int position;
        Parser(String text) { this.text = text; }
        private IOException error() { return new IOException("Некорректный JSON, позиция " + position); }
        private void whitespace() {
            while (position < text.length() && " \t\r\n".indexOf(text.charAt(position)) >= 0) position++;
        }
        private boolean take(char expected) {
            whitespace();
            if (position < text.length() && text.charAt(position) == expected) { position++; return true; }
            return false;
        }
        List<String> parse() throws IOException {
            List<String> result = new ArrayList<>();
            if (!take('[')) throw error();
            if (!take(']')) {
                do { result.add(string()); } while (take(','));
                if (!take(']')) throw error();
            }
            whitespace();
            if (position != text.length()) throw error();
            return result;
        }
        private String string() throws IOException {
            if (!take('"')) throw error();
            StringBuilder result = new StringBuilder();
            while (position < text.length()) {
                char c = text.charAt(position++);
                if (c == '"') return result.toString();
                if (c < 0x20) throw error();
                if (c != '\\') { result.append(c); continue; }
                if (position == text.length()) throw error();
                char escape = text.charAt(position++);
                switch (escape) {
                    case '"', '\\', '/' -> result.append(escape);
                    case 'n' -> result.append('\n');
                    case 'r' -> result.append('\r');
                    case 't' -> result.append('\t');
                    case 'b' -> result.append('\b');
                    case 'f' -> result.append('\f');
                    case 'u' -> {
                        if (position + 4 > text.length()) throw error();
                        int code = 0;
                        for (int i = 0; i < 4; i++) {
                            int digit = Character.digit(text.charAt(position++), 16);
                            if (digit < 0) throw error();
                            code = code * 16 + digit;
                        }
                        result.append((char) code);
                    }
                    default -> throw error();
                }
            }
            throw error();
        }
    }
}
