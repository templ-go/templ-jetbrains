package com.templ.templ.file.languages

import com.intellij.lang.html.HTMLLanguage
import com.intellij.lexer.Lexer
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.TokenType
import com.intellij.psi.templateLanguages.TemplateDataElementType
import com.intellij.psi.templateLanguages.TemplateDataModifications
import com.templ.templ.parsing._TemplLexer
import com.templ.templ.psi.TemplLeafElementType
import com.templ.templ.psi.TemplTypes
import org.jetbrains.annotations.NotNull

class HtmlElementType : TemplateDataElementType("HTML inside Templ",
    HTMLLanguage.INSTANCE,
    TemplTypes.HTML_FRAGMENT,
    TemplLeafElementType("TEMPL_NOT_HTML")
) {
    override fun appendCurrentTemplateToken(tokenEndOffset: Int, tokenText: CharSequence): TemplateDataModifications {
        // Detect if were inside an attribute value and if so, insert fake quotes to make the HTML parser happy.
        if (StringUtil.endsWithChar(tokenText, '=')) {
            return TemplateDataModifications.fromRangeToRemove(tokenEndOffset, "\"\"")
        }

        return super.appendCurrentTemplateToken(tokenEndOffset, tokenText)
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
        var braceCounter = -1
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
                TemplTypes.HTML_FRAGMENT,
                TemplTypes.SCRIPT_BODY -> {
                    val tokenModifications =
                        this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                    modifications.addAll(tokenModifications)
                }

                TokenType.WHITE_SPACE -> {
                    val tokenModifications =
                        this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                    modifications.addAll(tokenModifications)
                }

                TemplTypes.GO_PACKAGE_FRAGMENT,
                TemplTypes.GO_ROOT_FRAGMENT,
                TemplTypes.GO_IF,
                TemplTypes.GO_ELSE,
                TemplTypes.GO_FRAGMENT,
                TemplTypes.GO_SWITCH,
                TemplTypes.GO_CASE_FRAGMENT,
                TemplTypes.GO_DEFAULT_FRAGMENT,
                TemplTypes.GO_FOR,
                TemplTypes.DECL_GO_TOKEN,
                TemplTypes.TEMPL_FRAGMENT -> {
                    val emptyText = baseLexer.tokenSequence.toString().replace(Regex("\\S"), "")
                    modifications.addRangeToRemove(baseLexer.tokenStart, emptyText)
                    modifications.addOuterRange(
                        currentRange,
                        this.isInsertionToken(baseLexer.tokenType, emptyText)
                    )
                }

                TemplTypes.SCRIPT_DECL_START -> {
                    modifications.addRangeToRemove(baseLexer.tokenStart, "<")
                    val tokenModifications =
                        this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                    modifications.addAll(tokenModifications)
                    modifications.addRangeToRemove(baseLexer.tokenEnd, ">function")
                }

                TemplTypes.SCRIPT_FUNCTION_DECL -> {
                    // TODO: this is a hack to get the function name and arguments, it should really be done straight from the lexer.
                    val matches = "(.*)\\((.*)\\)".toRegex().find(baseLexer.tokenSequence)
                    if (matches != null && matches.groups.size == 3) {
                        val functionName = matches.groups[1]?.value
                        val functionArgs = matches.groups[2]?.value

                        if (functionName != null && functionArgs != null) {
                            val args = functionArgs.split(",").map { it.split(" ") }

                            modifications.addAll(
                                this.appendCurrentTemplateToken(
                                    baseLexer.tokenStart + functionName.length + 1,
                                    baseLexer.tokenSequence.subSequence(0, functionName.length + 1)
                                )
                            )

                            var pos = baseLexer.tokenStart + functionName.length + 1
                            for (arg in args) {
                                modifications.addAll(
                                    this.appendCurrentTemplateToken(
                                        pos,
                                        arg[0]
                                    )
                                )
                                pos += arg[0].length
                                if (arg.size > 1) {
                                    modifications.addOuterRange(
                                        TextRange(pos, pos + 1 + arg[1].length),
                                        this.isInsertionToken(baseLexer.tokenType, baseLexer.tokenSequence)
                                    )
                                    pos += 1 + arg[1].length
                                }
                                if (args.indexOf(arg) < args.size - 1) {
                                    modifications.addAll(
                                        this.appendCurrentTemplateToken(
                                            pos,
                                            ","
                                        )
                                    )
                                }
                            }
                            modifications.addAll(
                                this.appendCurrentTemplateToken(
                                    pos,
                                    ")"
                                )
                            )

                        }
                    }

                }

                TemplTypes.LBRACE -> {
                    if (braceCounter > 0) {
                        braceCounter++
                    }
                    if (baseLexer.state == _TemplLexer.IN_SCRIPT_DECLARATION_START) {
                        if (braceCounter == -1) {
                            braceCounter = 1
                        }
                        val tokenModifications =
                            this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                        modifications.addAll(tokenModifications)
                    } else {
                        modifications.addOuterRange(
                            currentRange,
                            this.isInsertionToken(baseLexer.tokenType, baseLexer.tokenSequence)
                        )
                    }
                }

                TemplTypes.RBRACE -> {
                    if (braceCounter >= 0) {
                        braceCounter--
                    }
                    if (baseLexer.state == _TemplLexer.IN_SCRIPT_DECLARATION_BODY && braceCounter == 0) {
                        val tokenModifications =
                            this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                        modifications.addAll(tokenModifications)
                        modifications.addRangeToRemove(baseLexer.tokenEnd, "</script>")
                        braceCounter = -1
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