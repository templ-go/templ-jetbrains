package com.templ.templ.highlighting

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType


class TemplHighlighter(lexer: TemplHighlightingLexer) : SyntaxHighlighterBase() {
    private val myLexer: TemplHighlightingLexer

    init {
        myLexer = lexer
    }

    override fun getHighlightingLexer(): Lexer {
        return myLexer
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<out TextAttributesKey> {
        if (tokenType is TemplElementType) {
            val scope = tokenType.getScope().scopeName ?: "";

            if (scope.startsWith("punctuation.definition.comment")) return COMMENT
            if (scope.startsWith("comment")) return COMMENT

            if (scope.startsWith("keyword")) return KEYWORD
            if (scope.startsWith("storage.type")) return KEYWORD

            if (scope.startsWith("string")) return STRING
            if (scope.startsWith("punctuation.definition.string")) return STRING
            if (scope.startsWith("entity.name.import")) return STRING


            if (scope.startsWith("entity.name.tag")) return arrayOf(DefaultLanguageHighlighterColors.METADATA)
            if (scope.startsWith("punctuation.definition.tag")) return arrayOf(DefaultLanguageHighlighterColors.METADATA)
            if (scope.startsWith("entity.other.attribute-name")) return arrayOf(DefaultLanguageHighlighterColors.IDENTIFIER)


            if (scope.startsWith("support.type.property-name")) return arrayOf(DefaultLanguageHighlighterColors.IDENTIFIER)
            return pack(null)
        }
        return pack(null)
    }
    private val KEYWORD = arrayOf(DefaultLanguageHighlighterColors.KEYWORD)
    private val STRING = arrayOf(DefaultLanguageHighlighterColors.STRING)
    private val COMMENT = arrayOf(DefaultLanguageHighlighterColors.LINE_COMMENT)
}