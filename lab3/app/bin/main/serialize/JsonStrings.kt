package serialize

import java.io.IOException
import java.util.ArrayList

// JSON-массив строк с представлениями значений ТД
internal object JsonStrings {

    fun quote(value: String): String {
        val result = StringBuilder("\"")
        for (i in 0 until value.length) {
            val c = value[i]
            when (c) {
                '"' -> result.append("\\\"")
                '\\' -> result.append("\\\\")
                '\n' -> result.append("\\n")
                '\r' -> result.append("\\r")
                '\t' -> result.append("\\t")
                '\b' -> result.append("\\b")
                '\u000C' -> result.append("\\f") // Исправлено: символ '\f' заменен на явный Юникод-код '\u000C'
                else -> {
                    if (c < 0x20.toChar()) {
                        result.append(String.format("\\u%04x", c.code))
                    } else {
                        result.append(c)
                    }
                }
            }
        }
        return result.append('"').toString()
    }

    @Throws(IOException::class)
    fun parse(text: String): List<String> = Parser(text).parse()

    private class Parser(private val text: String) {
        private var position = 0

        private fun error(): IOException = IOException("Некорректный JSON, позиция \$position")

        private fun whitespace() {
            while (position < text.length && " \t\r\n".indexOf(text[position]) >= 0) {
                position++
            }
        }

        private fun take(expected: Char): Boolean {
            whitespace()
            if (position < text.length && text[position] == expected) {
                position++
                return true
            }
            return false
        }

        @Throws(IOException::class)
        fun parse(): List<String> {
            val result = ArrayList<String>()
            if (!take('[')) throw error()
            if (!take(']')) {
                do {
                    result.add(string())
                } while (take(','))
                if (!take(']')) throw error()
            }
            whitespace()
            if (position != text.length) throw error()
            return result
        }

        @Throws(IOException::class)
        private fun string(): String {
            if (!take('"')) throw error()
            val result = StringBuilder()
            while (position < text.length) {
                var c = text[position++]
                if (c == '"') return result.toString()
                if (c < 0x20.toChar()) throw error()
                if (c != '\\') {
                    result.append(c)
                    continue
                }
                if (position == text.length) throw error()
                val escape = text[position++]
                when (escape) {
                    '"', '\\', '/' -> result.append(escape)
                    'n' -> result.append('\n')
                    'r' -> result.append('\r')
                    't' -> result.append('\t')
                    'b' -> result.append('\b')
                    'f' -> result.append('\u000C') // Исправлено: символ '\f' заменен на '\u000C'
                    'u' -> {
                        if (position + 4 > text.length) throw error()
                        var code = 0
                        for (i in 0 until 4) {
                            val digit = Character.digit(text[position++], 16)
                            if (digit < 0) throw error()
                            code = code * 16 + digit
                        }
                        result.append(code.toChar())
                    }
                    else -> throw error()
                }
            }
            throw error()
        }
    }
}
