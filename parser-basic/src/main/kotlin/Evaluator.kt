package com.sim2bty.parser

object Evaluator {
    fun evaluate(expression: Expression): Double {
        return when (expression) {
            is Expression.Value -> {
                return expression.number
            }
            is Expression.BinaryOp -> {
                val left = evaluate(expression.left)
                val right = evaluate(expression.right)
                when (expression.op) {
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
                }
            }
            is Expression.UnaryOp -> {
                val operand = evaluate(expression.operand)
                when (expression.op) {
                    Operator.PLUS -> operand
                    Operator.MINUS -> -operand
                    else -> throw Exception("Unknown operator ${expression.op}")
                }
            }
        }
    }
}
