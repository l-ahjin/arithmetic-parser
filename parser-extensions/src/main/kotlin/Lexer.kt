package com.sim2bty.parser

class Lexer(
    private val expression: String,
    private val registry: ExpressionRegistry,
) {
    private var pos = 0

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
                readIdentifier()
            }
            c == '(' -> {
                pos++
                Token.LParen
            }
            c == ')' -> {
                pos++
                Token.RParen
            }
            c == ',' -> {
                pos++
                Token.Comma
            }
            else -> {
                readOperation()
            }
        }
    }

    private fun createLexerError(
        message: String,
        startPos: Int,
        endPos: Int? = null,
    ): Nothing {
        val errorMessage =
            """
            $message
            position: $startPos${endPos?.let { ":$it" } ?: ""}
            $expression
            ${" ".repeat(startPos)}${if (endPos == null) "↑" else "~".repeat(endPos - startPos)}
            """.trimIndent()
        throw Exception(errorMessage)
    }

    private fun readNumber(): Token.Number {
        val start = pos
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
                    if (c == ',' && (pos + 1 >= expression.length || !expression[pos + 1].isDigit())) break
                }
                else -> {
                    break
                }
            }
            pos++
        }
        val number = sb.toString().toDoubleOrNull() ?: createLexerError("Invalid number", start, pos)
        return Token.Number(number)
    }

    private fun readIdentifier(): Token {
        val start = pos
        val end =
            generateSequence(pos) { it + 1 }
                .takeWhile { it < expression.length && expression[it].isLetter() }
                .lastOrNull() ?: pos
        val identifier = expression.substring(start..end)
        pos = end + 1

        return when {
            registry.findBinaryOperator(identifier) != null -> Token.Operation(identifier)
            registry.findFunction(identifier) != null -> Token.Function(identifier)
            else -> createLexerError("Unexpected operator or function", start, end)
        }
    }

    private fun readOperation(): Token.Operation {
        val start = pos
        val identifier =
            registry.identifiers
                .find { op ->
                    if (!op.startsWith(expression[start])) return@find false
                    val end = start + op.length - 1
                    end < expression.length && expression.substring(start..end) == op
                }

        return if (identifier != null) {
            pos = start + identifier.length
            Token.Operation(identifier)
        } else {
            createLexerError("Unexpected character", start)
        }
    }
}
