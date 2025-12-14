package com.sim2bty.parser

class Evaluator(
    private val registry: ExpressionRegistry,
) {
    fun evaluate(node: Node): Double =
        when (node) {
            is Node.Value -> {
                node.number
            }
            is Node.BinaryOp -> {
                val left = evaluate(node.left)
                val right = evaluate(node.right)
                val operator = registry.findBinaryOperator(node.operator) ?: throw Exception("Unknown operator ${node.operator}")
                operator.evaluator(left, right)
            }
            is Node.PrefixOp -> {
                val operand = evaluate(node.operand)
                val operator =
                    registry.findPrefixOperator(node.operator)
                        ?: throw Exception("Unknown prefix operator ${node.operator}")
                operator.evaluator(operand)
            }
            is Node.PostfixOp -> {
                val operand = evaluate(node.operand)
                val operator =
                    registry.findPostfixOperator(node.operator)
                        ?: throw Exception("Unknown postfix operator ${node.operator}")
                operator.evaluator(operand)
            }
            is Node.FunctionCall -> {
                val function = registry.findFunction(node.name) ?: throw Exception("Unknown function ${node.name}")
                val arguments = node.arguments.map { evaluate(it) }
                function.evaluator(arguments)
            }
        }
}
