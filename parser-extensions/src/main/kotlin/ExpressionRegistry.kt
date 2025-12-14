package com.sim2bty.parser

class ExpressionRegistry {
    private val binaryOperators = mutableSetOf<BinaryOperator>()
    private val unaryOperators = mutableSetOf<UnaryOperator>()
    private val functions = mutableSetOf<Function>()
    private val _identifiers = mutableSetOf<String>()
    private val unaryIdentifiers = mutableSetOf<String>()
    val identifiers
        get() =
            (_identifiers + unaryIdentifiers).distinct().sortedByDescending { it.length }

    fun addBinaryOperator(operator: BinaryOperator) {
        val duplicatedIdentifier = operator.identifier.find { _identifiers.contains(it.lowercase()) }
        require(duplicatedIdentifier == null) { "Binary Operator's identifier $duplicatedIdentifier already exists" }
        binaryOperators.add(operator)
        _identifiers.addAll(operator.identifier.map(String::lowercase))
    }

    fun addUnaryOperator(operator: UnaryOperator) {
        val duplicatedIdentifier = operator.identifier.find { unaryIdentifiers.contains(it.lowercase()) }
        require(duplicatedIdentifier == null) { "Unary Operator's identifier $duplicatedIdentifier already exists" }
        unaryOperators.add(operator)
        unaryIdentifiers.addAll(operator.identifier.map(String::lowercase))
    }

    fun addFunction(function: Function) {
        val duplicatedFunction = functions.find { it.name == function.name }
        require(duplicatedFunction == null) { "Function ${function.name} already exists" }
        functions.add(function)
    }

    fun addBinaryOperators(operators: List<BinaryOperator>) {
        operators.forEach {
            addBinaryOperator(it)
        }
    }

    fun addUnaryOperators(operators: List<UnaryOperator>) {
        operators.forEach {
            addUnaryOperator(it)
        }
    }

    fun addFunctions(functions: List<Function>) {
        functions.forEach {
            addFunction(it)
        }
    }

    fun findBinaryOperator(identifier: String) =
        binaryOperators.find { op -> op.identifier.any { it.equals(identifier, ignoreCase = true) } }

    fun findPrefixOperator(identifier: String) =
        unaryOperators.find { op -> op.type == UnaryOperatorType.PREFIX && op.identifier.any { it.equals(identifier, ignoreCase = true) } }

    fun findPostfixOperator(identifier: String) =
        unaryOperators.find { op -> op.type == UnaryOperatorType.POSTFIX && op.identifier.any { it.equals(identifier, ignoreCase = true) } }

    fun findFunction(name: String) = functions.find { it.name.equals(name, ignoreCase = true) }
}
