package com.sim2bty.parser

sealed interface Token {
    data class Number(
        val value: Double,
    ) : Token

    data class Operation(
        val identifier: String,
    ) : Token

    data class Function(
        val name: String,
    ) : Token

    data object LParen : Token

    data object RParen : Token

    data object Comma : Token

    data object EOF : Token
}
