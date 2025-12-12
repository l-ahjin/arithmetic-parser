package com.sim2bty.parser

sealed interface Expression {
    data class Value(
        val number: Double,
    ) : Expression

    data class BinaryOp(
        val left: Expression,
        val op: Operator,
        val right: Expression,
    ) : Expression

    data class UnaryOp(
        val op: Operator,
        val operand: Expression,
    ) : Expression

    data class Function(
        val op: Operator,
        val args: List<Expression>,
    ) : Expression
}

enum class Operator {
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO,
    ABS,
    MIN,
    MAX,
}
