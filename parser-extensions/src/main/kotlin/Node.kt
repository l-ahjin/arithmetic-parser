package com.sim2bty.parser

sealed interface Node {
    data class Value(
        val number: Double,
    ) : Node

    data class BinaryOp(
        val left: Node,
        val operator: String,
        val right: Node,
    ) : Node

    data class PrefixOp(
        val operator: String,
        val operand: Node,
    ) : Node

    data class PostfixOp(
        val operand: Node,
        val operator: String,
    ) : Node

    data class FunctionCall(
        val name: String,
        val arguments: List<Node>,
    ) : Node
}
