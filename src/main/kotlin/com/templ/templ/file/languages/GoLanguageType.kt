package com.templ.templ.file.languages

import com.goide.GoLanguage
import com.intellij.lang.Language
import com.intellij.lexer.Lexer
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.TokenType
import com.intellij.psi.templateLanguages.TemplateDataElementType
import com.intellij.psi.templateLanguages.TemplateDataModifications
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider
import com.templ.templ.parsing._TemplLexer
import com.templ.templ.psi.TemplLeafElementType
import com.templ.templ.psi.TemplTypes
import org.jetbrains.annotations.NotNull

class GoLanguageType : TemplateDataElementType(
    "Go inside Templ",
    GoLanguage.INSTANCE,
    TemplTypes.GO_ROOT_FRAGMENT,
    TemplLeafElementType("TEMPL_NOT_GO")
) {
    override fun getTemplateFileLanguage(viewProvider: TemplateLanguageFileViewProvider): Language {
        return GoLanguage.INSTANCE
    }

    private fun getRangeDump(range: @NotNull TextRange, sequence: @NotNull CharSequence): @NotNull String {
        return "'" + StringUtil.escapeLineBreak(range.subSequence(sequence).toString()) + "' " + range
    }

    override fun collectTemplateModifications(
        sourceCode: @NotNull CharSequence,
        baseLexer: @NotNull Lexer
    ): @NotNull TemplateDataModifications {

        val modifications = TemplateDataModifications()
        baseLexer.start(sourceCode)
        var currentRange = TextRange.EMPTY_RANGE

        var tokenCounter = 0
        var braceCounter = 0
        while (baseLexer.tokenType != null) {
            ++tokenCounter
            if (tokenCounter % 1000 == 0) {
                ProgressManager.checkCanceled()
            }

            val newRange = TextRange.create(baseLexer.tokenStart, baseLexer.tokenEnd)

            assert(currentRange.endOffset == newRange.startOffset) {
                "Inconsistent tokens stream from $baseLexer: " + getRangeDump(
                    currentRange,
                    sourceCode
                ) + " followed by " + getRangeDump(newRange, sourceCode)
            }

            currentRange = newRange
            when (baseLexer.tokenType) {
                TemplTypes.GO_ROOT_FRAGMENT,
                TemplTypes.GO_IF,
                TemplTypes.GO_ELSE,
                TemplTypes.GO_FRAGMENT,
                TemplTypes.GO_SWITCH_START_FRAGMENT,
                TemplTypes.GO_CASE_FRAGMENT,
                TemplTypes.GO_DEFAULT_FRAGMENT,
                TemplTypes.GO_FOR_START_FRAGMENT,
                TemplTypes.DECL_GO_TOKEN -> {
                    modifications.addAll(
                        this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                    )
                }

                TokenType.WHITE_SPACE -> {
                    modifications.addAll(
                        this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                    )
                }

                TemplTypes.HTML_FRAGMENT,
                TemplTypes.SCRIPT_BODY,
                TemplTypes.TEMPL_FRAGMENT,
                TemplTypes.COMPONENT_IMPORT_START -> {
                    val emptyText = baseLexer.tokenSequence.toString().replace(Regex("\\S"), " ")
                    modifications.addRangeToRemove(baseLexer.tokenStart, emptyText)
                    modifications.addOuterRange(currentRange)
                }

                TemplTypes.GO_PACKAGE_FRAGMENT -> {
                    modifications.addAll(
                        this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                    )
                    modifications.addRangeToRemove(baseLexer.tokenEnd, "\nimport \"github.com/a-h/templ\"")
                }

                TemplTypes.GO_EXPR -> {
                    if (baseLexer.state == _TemplLexer.IN_EXPR) {
                        modifications.addRangeToRemove(baseLexer.tokenStart, "var _ string = ")
                        modifications.addAll(
                            this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                        )
                    } else {
                        modifications.addOuterRange(
                            currentRange,
                            this.isInsertionToken(baseLexer.tokenType, baseLexer.tokenSequence)
                        )
                    }
                }

                TemplTypes.COMPONENT_REFERENCE -> {
                    val tokenModifications =
                        this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                    modifications.addAll(tokenModifications)
                    modifications.addRangeToRemove(baseLexer.tokenEnd, ".Render(nil, nil);")
                }

                TemplTypes.HTML_DECL_START -> {
                    modifications.addRangeToRemove(baseLexer.tokenStart, "func")
                    modifications.addOuterRange(
                        currentRange,
                        this.isInsertionToken(baseLexer.tokenType, baseLexer.tokenSequence)
                    )
                }

                TemplTypes.LBRACE,
                TemplTypes.RBRACE -> {
                    if (arrayOf(
                            _TemplLexer.IN_TEMPL_DECLARATION_START,
                            _TemplLexer.IN_TEMPL_DECLARATION_BODY,
                            _TemplLexer.IN_EXPR,
                            _TemplLexer.IN_HTML_TAG_OPENER,
                            _TemplLexer.IN_COMPONENT_IMPORT_CHILDREN_BLOCK_START,
                            _TemplLexer.IN_GO_BLOCK_START,
                        ).contains(baseLexer.state)
                    ) {
                        if (baseLexer.tokenType === TemplTypes.LBRACE) {
                            braceCounter++
                        } else {
                            braceCounter--
                        }
                        if (baseLexer.tokenType === TemplTypes.LBRACE && baseLexer.state == _TemplLexer.IN_TEMPL_DECLARATION_START) {
                            modifications.addRangeToRemove(baseLexer.tokenStart, "templ.Component ")
                        }
                        if (baseLexer.tokenType === TemplTypes.RBRACE && baseLexer.state == _TemplLexer.IN_TEMPL_DECLARATION_BODY && braceCounter == 0) {
                            modifications.addRangeToRemove(baseLexer.tokenStart, "return nil")
                        }
                        modifications.addAll(
                            this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                        )
                    } else {
                        modifications.addOuterRange(
                            currentRange,
                            this.isInsertionToken(baseLexer.tokenType, baseLexer.tokenSequence)
                        )
                    }
                }

                else -> {
                    modifications.addOuterRange(
                        currentRange,
                        this.isInsertionToken(baseLexer.tokenType, baseLexer.tokenSequence)
                    )
                }
            }
            baseLexer.advance()
        }

        return modifications
    }
}