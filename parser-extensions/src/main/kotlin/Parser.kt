package com.sim2bty.parser

class Parser(
    private val lexer: Lexer,
    private val registry: ExpressionRegistry,
) {
    private var currentToken = lexer.nextToken()

    fun parse(): Node {
        val result = expression(0)
        if (currentToken !is Token.EOF) {
            throw Exception("Unexpected end of expression")
        }
        return result
    }

    private fun consume() {
        currentToken = lexer.nextToken()
    }

    private fun expression(precedence: Int): Node {
        tailrec fun expression(node: Node): Node {
            val token = currentToken
            if (token !is Token.Operation) return node
            val op = registry.findBinaryOperator(token.identifier) ?: return node
            if (op.precedence < precedence) return node
            consume()

            val nextPrecedence =
                when (op.associativity) {
                    Associativity.LEFT -> op.precedence + 1
                    Associativity.RIGHT -> op.precedence
                }

            val right = expression(nextPrecedence)
            val newNode = Node.BinaryOp(node, token.identifier, right)
            return expression(newNode)
        }
        val node = primary()
        return expression(node)
    }

    private fun functionCall(name: String): Node {
        val func = registry.findFunction(name) ?: throw Exception("Function $name not found")
        consume()

        if (currentToken != Token.LParen) {
            throw Exception("Expected '(' after function name '$name'")
        }
        consume()

        val args = mutableListOf<Node>()
        if (currentToken !is Token.RParen) {
            args.add(expression(0))
            while (currentToken is Token.Comma) {
                consume()
                args.add(expression(0))
            }
        }

        if (currentToken !is Token.RParen) {
            throw Exception("Expected ')' to close argument list for function '$name', but found $currentToken")
        }
        consume()

        if (args.size != func.arity) {
            throw Exception("Function $name require ${func.arity} arguments but ${args.size} found")
        }
        return Node.FunctionCall(name, args)
    }

    private fun primary(): Node {
        var node =
            when (val token = currentToken) {
                is Token.Number -> {
                    consume()
                    Node.Value(token.value)
                }
                Token.LParen -> {
                    consume()
                    val node = expression(0)
                    if (currentToken != Token.RParen) {
                        throw Exception("Expected ')' but $currentToken")
                    }
                    consume()
                    node
                }
                is Token.Operation -> {
                    val prefixOp =
                        registry.findPrefixOperator(token.identifier)
                            ?: throw Exception("Unexpected prefix operation: ${token.identifier}")
                    consume()
                    val operand = expression(prefixOp.precedence)
                    Node.PrefixOp(token.identifier, operand)
                }
                is Token.Function -> {
                    functionCall(token.name)
                }
                else -> {
                    throw Exception("Unexpected token: $token")
                }
            }

        tailrec fun postfixOp(node: Node): Node {
            val token = currentToken
            if (token !is Token.Operation) return node
            registry.findPostfixOperator(token.identifier) ?: return node

            consume()
            val newNode = Node.PostfixOp(node, token.identifier)
            return postfixOp(newNode)
        }
        node = postfixOp(node)
        return node
    }
}
