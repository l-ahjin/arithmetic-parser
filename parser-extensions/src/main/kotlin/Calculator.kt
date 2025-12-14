package com.sim2bty.parser

import com.sim2bty.parser.Calculator.Companion.standardExpressionRegistry
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

class Calculator(
    private val registry: ExpressionRegistry,
) {
    private val evaluator = Evaluator(registry)

    fun calculate(expression: String): Double {
        val lexer = Lexer(expression, registry)
        val parser = Parser(lexer, registry)
        val expr = parser.parse()
        return evaluator.evaluate(expr)
    }

    companion object {
        val standardExpressionRegistry: ExpressionRegistry
            get() {
                val registry = ExpressionRegistry()
                registry.addBinaryOperators(binaryOperators)
                registry.addUnaryOperators(unaryOperators)
                registry.addFunctions(functions)
                return registry
            }
        private val binaryOperators =
            listOf(
                // Comparison Operators
                BinaryOperator(identifier = setOf("=="), precedence = 0, evaluator = { l, r -> if (l == r) 1.0 else 0.0 }),
                BinaryOperator(identifier = setOf(">"), precedence = 0, evaluator = { l, r -> if (l > r) 1.0 else 0.0 }),
                BinaryOperator(identifier = setOf(">="), precedence = 0, evaluator = { l, r -> if (l >= r) 1.0 else 0.0 }),
                BinaryOperator(identifier = setOf("<"), precedence = 0, evaluator = { l, r -> if (l < r) 1.0 else 0.0 }),
                BinaryOperator(identifier = setOf("<="), precedence = 0, evaluator = { l, r -> if (l <= r) 1.0 else 0.0 }),
                // Bitwise Logical Operations
                BinaryOperator(identifier = setOf("and"), precedence = 1, evaluator = { l, r ->
                    (l.roundToInt() and r.roundToInt()).toDouble()
                }),
                BinaryOperator(identifier = setOf("or"), precedence = 1, evaluator = { l, r ->
                    (l.roundToInt() or r.roundToInt()).toDouble()
                }),
                BinaryOperator(identifier = setOf("xor"), precedence = 1, evaluator = { l, r ->
                    (l.roundToInt() xor r.roundToInt()).toDouble()
                }),
                BinaryOperator(identifier = setOf("nand"), precedence = 1, evaluator = { l, r ->
                    (l.roundToInt() and r.roundToInt()).inv().toDouble()
                }),
                BinaryOperator(identifier = setOf("nor"), precedence = 1, evaluator = { l, r ->
                    (l.roundToInt() or r.roundToInt()).inv().toDouble()
                }),
                // Bitwise Shift
                BinaryOperator(identifier = setOf(">>"), precedence = 2, evaluator = { l, r ->
                    (l.roundToInt() shr r.roundToInt()).toDouble()
                }),
                BinaryOperator(identifier = setOf("<<"), precedence = 2, evaluator = { l, r ->
                    (l.roundToInt() shl r.roundToInt()).toDouble()
                }),
                BinaryOperator(identifier = setOf(">>>"), precedence = 2, evaluator = { l, r ->
                    (l.roundToInt() ushr r.roundToInt()).toDouble()
                }),
                // Addition, Subtraction
                BinaryOperator(identifier = setOf("+", "plus"), precedence = 3, evaluator = { l, r -> l + r }),
                BinaryOperator(identifier = setOf("-", "minus"), precedence = 3, evaluator = { l, r -> l - r }),
                // Multiplication, Division, Modulo
                BinaryOperator(identifier = setOf("*", "x", "mul", "times"), precedence = 4, evaluator = { l, r -> l * r }),
                BinaryOperator(identifier = setOf("/", "div"), precedence = 4, evaluator = { l, r ->
                    if (r == 0.0) Double.NaN else l / r
                }),
                BinaryOperator(identifier = setOf("%", "mod", "rem"), precedence = 4, evaluator = { l, r ->
                    if (r == 0.0) Double.NaN else l % r
                }),
                // Exponentiation
                BinaryOperator(identifier = setOf("^", "pow"), precedence = 5, Associativity.RIGHT, evaluator = { l, r -> l.pow(r) }),
            )

        private fun factorial(n: Double): Double {
            require(!(n < 0 || n.toInt().toDouble() != n)) { "Factorial is only defined for non-negative integers" }
            return if (n == 0.0) 1.0 else n * factorial(n - 1)
        }

        private val unaryOperators =
            listOf(
                UnaryOperator(identifier = setOf("+"), type = UnaryOperatorType.PREFIX, precedence = 6, evaluator = { it }),
                UnaryOperator(identifier = setOf("-"), type = UnaryOperatorType.PREFIX, precedence = 6, evaluator = { -it }),
                UnaryOperator(identifier = setOf("!"), type = UnaryOperatorType.POSTFIX, precedence = 6, evaluator = { factorial(it) }),
            )

        private val functions =
            listOf(
                Function(name = "abs", arity = 1, evaluator = { args -> abs(args[0]) }),
                Function(name = "not", arity = 1, evaluator = { args -> args[0].roundToInt().inv().toDouble() }),
                Function(name = "min", arity = 2, evaluator = { args -> min(args[0], args[1]) }),
                Function(name = "max", arity = 2, evaluator = { args -> max(args[0], args[1]) }),
            )
    }
}

fun main() {
    val calculator = Calculator(standardExpressionRegistry)
    val input = "max(10, 5) + abs(-10) + min(-5, 10) times 5 + 3 - 7 + (6 / 2 + (5 minus 2)) % 3 and 5 > 1"
    val result = calculator.calculate(input)
    println(result)
}
