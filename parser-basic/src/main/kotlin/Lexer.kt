package com.sim2bty.parser

class Lexer(
    val expression: String,
) {
    private var pos = 0

    private val keyword =
        mapOf(
            "plus" to Token.Plus,
            "minus" to Token.Minus,
            "times" to Token.Mul,
            "div" to Token.Div,
            "mod" to Token.Mod,
        )

    fun nextToken(): Token {
        while (pos < expression.length && expression[pos].isWhitespace()) {
            pos++
        }
        if (pos >= expression.length) {
            return Token.EOF
        }

        val c = expression[pos]
        return when {
            c.isDigit() -> {
                readNumber()
            }
            c.isLetter() -> {
                readKeyword()
            }
            c == '(' -> {
                pos++
                Token.LParen
            }
            c == ')' -> {
                pos++
                Token.RParen
            }
            c == '+' -> {
                pos++
                Token.Plus
            }
            c == '-' -> {
                pos++
                Token.Minus
            }
            c in "*x" -> {
                pos++
                Token.Mul
            }
            c == '/' -> {
                pos++
                Token.Div
            }
            c == '%' -> {
                pos++
                Token.Mod
            }
            else -> {
                throw Exception(
                    """
                    Unexpected character '$c'
                    $expression
                    ${" ".repeat(pos)}↑
                    """.trimIndent(),
                )
            }
        }
    }

    private fun readNumber(): Token {
        val sb = StringBuilder()
        var hasDecimal = false
        var hasSeparator = false

        while (pos < expression.length) {
            val c = expression[pos]
            when {
                c.isDigit() -> {
                    sb.append(c)
                }
                c == '.' -> {
                    if (hasDecimal) break
                    hasDecimal = true
                    sb.append(c)
                }
                c in ",_" -> {
                    if (hasSeparator) break
                    hasSeparator = true
                }
                else -> {
                    break
                }
            }
            pos++
        }
        val number = sb.toString().toDoubleOrNull() ?: throw Exception("Invalid number")
        return Token.Number(number)
    }

    private fun readKeyword(): Token {
        val start = pos
        val sb = StringBuilder()
        while (pos < expression.length && expression[pos].isLetter()) {
            sb.append(expression[pos])
            pos++
        }
        val text = sb.toString().lowercase()
        return keyword[text] ?: throw Exception(
            """
            Unexpected keyword '$text'
            $expression
            ${" ".repeat(start)}${"~".repeat(pos - start)}
            """.trimIndent(),
        )
    }
}
