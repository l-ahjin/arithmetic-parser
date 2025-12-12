package com.sim2bty.parser

sealed interface Token {
    data class Number(
        val value: Double,
    ) : Token

    data object EOF : Token

    data object LParen : Token

    data object RParen : Token

    data object Plus : Token

    data object Minus : Token

    data object Mul : Token

    data object Div : Token

    data object Mod : Token

    data object Abs : Token

    data object Min : Token

    data object Max : Token

    data object Comma : Token
}
