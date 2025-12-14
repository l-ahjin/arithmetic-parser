package com.sim2bty.parser

data class BinaryOperator(
    val identifier: Set<String>,
    val precedence: Int,
    val associativity: Associativity = Associativity.LEFT,
    val evaluator: (Double, Double) -> Double,
)

enum class UnaryOperatorType {
    PREFIX,
    POSTFIX,
}

data class UnaryOperator(
    val identifier: Set<String>,
    val type: UnaryOperatorType,
    val precedence: Int,
    val evaluator: (Double) -> Double,
)

data class Function(
    val name: String,
    val arity: Int,
    val evaluator: (List<Double>) -> Double,
)

enum class Associativity {
    LEFT,
    RIGHT,
}
