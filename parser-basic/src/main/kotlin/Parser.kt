package com.sim2bty.parser

class Parser(
    private val lexer: Lexer,
) {
    private var currentToken = lexer.nextToken()

    fun parse(): Expression = expr()

    private fun consume() {
        currentToken = lexer.nextToken()
    }

    private fun factor(): Expression {
        when (val token = currentToken) {
            is Token.Number -> {
                consume()
                return Expression.Value(token.value)
            }
            Token.LParen -> {
                consume()
                val node = expr()
                if (currentToken != Token.RParen) {
                    throw Exception()
                }
                consume()
                return node
            }
            Token.Plus -> {
                consume()
                val operand = factor()
                return Expression.UnaryOp(Operator.PLUS, operand)
            }
            Token.Minus -> {
                consume()
                val operand = factor()
                return Expression.UnaryOp(Operator.MINUS, operand)
            }
            Token.Abs, Token.Min, Token.Max -> {
                val op =
                    when (currentToken) {
                        is Token.Abs -> Operator.ABS
                        is Token.Min -> Operator.MIN
                        is Token.Max -> Operator.MAX
                        else -> throw Exception("Unreachable")
                    }
                consume()
                if (currentToken != Token.LParen) {
                    throw Exception("Expected '(' after function")
                }
                consume()

                val args = mutableListOf<Expression>()
                if (currentToken != Token.RParen) {
                    args.add(expr())
                    while (currentToken == Token.Comma) {
                        consume()
                        args.add(expr())
                    }
                }

                if (currentToken != Token.RParen) {
                    throw Exception("Expected ')' after arguments")
                }
                consume()
                return Expression.Function(op, args)
            }
            else -> {
                throw Exception("unexpected token $token")
            }
        }
    }

    private fun term(): Expression {
        tailrec fun term(node: Expression): Expression {
            val token = currentToken
            if (token !is Token.Mul && token !is Token.Div && token !is Token.Mod) {
                return node
            }
            val op = if (token is Token.Mul) Operator.MULTIPLY else if (token is Token.Div) Operator.DIVIDE else Operator.MODULO

            consume()
            val right = factor()
            val newNode = Expression.BinaryOp(node, op, right)
            return term(newNode)
        }
        return term(factor())
    }

    private fun expr(): Expression {
        tailrec fun expr(node: Expression): Expression {
            val token = currentToken
            if (token !is Token.Plus && token !is Token.Minus) {
                return node
            }
            val op = if (token is Token.Plus) Operator.PLUS else Operator.MINUS
            consume()
            val right = term()
            val newNode = Expression.BinaryOp(node, op, right)
            return expr(newNode)
        }
        return expr(term())
    }
}
