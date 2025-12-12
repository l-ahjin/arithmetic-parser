package com.sim2bty.parser

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object Evaluator {
    fun evaluate(expression: Expression): Double {
        return when (expression) {
            is Expression.Value -> {
                return expression.number
            }
            is Expression.BinaryOp -> {
                evaluateBinaryOp(expression)
            }
            is Expression.UnaryOp -> {
                evaluateUnaryOp(expression)
            }
            is Expression.Function -> {
                evaluateFunction(expression)
            }
        }
    }

    private fun evaluateBinaryOp(expression: Expression.BinaryOp): Double {
        val left = evaluate(expression.left)
        val right = evaluate(expression.right)
        return when (expression.op) {
            Operator.PLUS -> {
                left + right
            }
            Operator.MINUS -> {
                left - right
            }
            Operator.MULTIPLY -> {
                left * right
            }
            Operator.DIVIDE -> {
                if (right == 0.0) {
                    Double.NaN
                }
                left / right
            }
            Operator.MODULO -> {
                if (right == 0.0) {
                    Double.NaN
                }
                left % right
            }
            else -> {
                throw Exception("Unknown operator ${expression.op}")
            }
        }
    }

    private fun evaluateUnaryOp(expression: Expression.UnaryOp): Double {
        val operand = evaluate(expression.operand)
        return when (expression.op) {
            Operator.PLUS -> operand
            Operator.MINUS -> -operand
            else -> throw Exception("Unknown operator ${expression.op}")
        }
    }

    private fun evaluateFunction(expression: Expression.Function): Double =
        when (expression.op) {
            Operator.ABS -> {
                if (expression.args.size != 1) {
                    throw Exception("abs function requires 1 argument")
                }
                val arg = evaluate(expression.args[0])
                abs(arg)
            }
            Operator.MIN, Operator.MAX -> {
                if (expression.args.size != 2) {
                    throw Exception("min/max function requires 2 arguments")
                }
                val arg1 = evaluate(expression.args[0])
                val arg2 = evaluate(expression.args[1])
                if (expression.op == Operator.MIN) {
                    min(arg1, arg2)
                } else {
                    max(arg1, arg2)
                }
            }
            else -> {
                throw Exception("Unknown function ${expression.op}")
            }
        }
}
