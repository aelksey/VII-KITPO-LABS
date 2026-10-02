package serialize

import java.io.IOException
import java.util.ArrayList
import java.util.List

object JsonStrings {

  def quote(value: String): String = {
    val result = new StringBuilder("\"")
    for (i <- 0 until value.length()) {
      val c = value.charAt(i)
      c match {
        case '"'  => { result.append("\\\"") }
        case '\\' => { result.append("\\\\") }
        case '\n' => { result.append("\\n") }
        case '\r' => { result.append("\\r") }
        case '\t' => { result.append("\\t") }
        case '\b' => { result.append("\\b") }
        case '\f' => { result.append("\\f") }
        case _    => {
          if (c < 0x20) {
            result.append(java.lang.String.format("\\u%04x", java.lang.Integer.valueOf(c.toInt)))
          } else {
            result.append(c)
          }
        }
      }
    }
    result.append('"').toString()
  }

  @throws[IOException]
  def parse(text: String): List[String] = {
    new Parser(text).parse()
  }

  private final class Parser(private val text: String) {
    private var position: Int = 0

    private def error(): IOException = {
      new IOException("Некорректный JSON, позиция " + position)
    }

    private def whitespace(): Unit = {
      while ((position < text.length()) && (" \t\r\n".indexOf(text.charAt(position).toInt) >= 0)) {
        position = position + 1
      }
    }

    private def take(expected: Char): Boolean = {
      whitespace()
      if ((position < text.length()) && (text.charAt(position) == expected)) {
        position = position + 1
        true
      } else {
        false
      }
    }

    @throws[IOException]
    def parse(): List[String] = {
      val result = new ArrayList[String]()
      if (!take('[')) {
        throw error()
      }
      if (!take(']')) {
        var continueLoop = true
        while (continueLoop) {
          result.add(string())
          if (!take(',')) {
            continueLoop = false
          }
        }
        if (!take(']')) {
          throw error()
        }
      }
      whitespace()
      if (position != text.length()) {
        throw error()
      }
      result
    }

    @throws[IOException]
    private def string(): String = {
      if (!take('"')) {
        throw error()
      }
      val result = new StringBuilder()
      var loop = true
      while ((position < text.length()) && loop) {
        val c = text.charAt(position)
        position = position + 1
        
        if (c == '"') {
          loop = false
        } else {
          if (c < 0x20) {
            throw error()
          }
          if (c != '\\') {
            result.append(c)
          } else {
            if (position == text.length()) {
              throw error()
            }
            val escape = text.charAt(position)
            position = position + 1
            
            escape match {
              case '"' | '\\' | '/' => { result.append(escape) }
              case 'n'  => { result.append('\n') }
              case 'r'  => { result.append('\r') }
              case 't'  => { result.append('\t') }
              case 'b'  => { result.append('\b') }
              case 'f'  => { result.append('\f') }
              case 'u'  => {
                if (position + 4 > text.length()) {
                  throw error()
                }
                var code = 0
                for (i <- 0 until 4) {
                  val digit = Character.digit(text.charAt(position), 16)
                  position = position + 1
                  if (digit < 0) {
                    throw error()
                  }
                  code = code * 16 + digit
                }
                result.append(code.toChar)
              }
              case _ => {
                throw error()
              }
            }
          }
        }
      }
      if (loop) {
        throw error()
      }
      result.toString()
    }
  }
}
