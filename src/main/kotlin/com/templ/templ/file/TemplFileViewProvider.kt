package com.templ.templ.file

import com.goide.GoLanguage
import com.intellij.lang.Language
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.lang.html.HTMLLanguage
import com.intellij.lexer.Lexer
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.TokenType
import com.intellij.psi.impl.source.PsiFileImpl
import com.intellij.psi.templateLanguages.TemplateDataElementType
import com.intellij.psi.templateLanguages.TemplateDataModifications
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider
import com.templ.templ.TemplLanguage
import com.templ.templ.parsing._TemplLexer
import com.templ.templ.psi.TemplLeafElementType
import com.templ.templ.psi.TemplTypes
import org.jetbrains.annotations.NotNull

class TemplFileViewProvider(manager: PsiManager, virtualFile: VirtualFile, eventSystemEnabled: Boolean) :
    MultiplePsiFilesPerDocumentFileViewProvider(manager, virtualFile, eventSystemEnabled), TemplateLanguageFileViewProvider {

    private val htmlElementType = object : TemplateDataElementType("HTML inside Templ", HTMLLanguage.INSTANCE, TemplTypes.HTML_FRAGMENT, TemplLeafElementType("TEMPL_NOT_HTML")) {
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
                if (arrayOf(
                        TemplTypes.HTML_FRAGMENT,
                        TemplTypes.SCRIPT_BODY,
                    ).contains(baseLexer.tokenType)) {
                    val tokenModifications =
                        this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                    modifications.addAll(tokenModifications)
                } else if (baseLexer.tokenType === TemplTypes.SCRIPT_DECL_START) {
                    modifications.addRangeToRemove(baseLexer.tokenStart, "<")
                    val tokenModifications =
                        this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                    modifications.addAll(tokenModifications)
                    modifications.addRangeToRemove(baseLexer.tokenEnd, ">function")
                } else if (baseLexer.tokenType === TemplTypes.SCRIPT_FUNCTION_DECL) {
                    // TODO: this is a hack to get the function name and arguments, it should really be done straight from the lexer.
                    val matches = "(.*)\\((.*)\\)".toRegex().find(baseLexer.tokenSequence)
                    if (matches != null && matches.groups.size == 3) {
                        val functionName = matches.groups[1]?.value
                        val functionArgs = matches.groups[2]?.value

                        if (functionName != null && functionArgs != null) {
                            val args = functionArgs.split(",").map { it.split(" ") }

                            modifications.addAll(this.appendCurrentTemplateToken(
                                baseLexer.tokenStart + functionName.length+1,
                                baseLexer.tokenSequence.subSequence(0, functionName.length+1))
                            )

                            var pos = baseLexer.tokenStart + functionName.length+1
                            for (arg in args) {
                                modifications.addAll(this.appendCurrentTemplateToken(
                                    pos,
                                    arg[0])
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
                            modifications.addAll(this.appendCurrentTemplateToken(
                                pos,
                                ")"
                            ))

                        }
                    }

                } else if (baseLexer.tokenType === TemplTypes.LBRACE) {
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
                } else if (baseLexer.tokenType === TemplTypes.RBRACE) {
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
                } else if (baseLexer.tokenType === TokenType.WHITE_SPACE) {
                    val tokenModifications =
                        this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                    modifications.addAll(tokenModifications)
                } else if (arrayOf(
                        TemplTypes.GO_ROOT_FRAGMENT,
                        TemplTypes.GO_IF_START_FRAGMENT,
                        TemplTypes.GO_ELSE_IF_START_FRAGMENT,
                        TemplTypes.GO_ELSE_START_FRAGMENT,
                        TemplTypes.GO_FRAGMENT,
                        TemplTypes.GO_SWITCH_START_FRAGMENT,
                        TemplTypes.GO_CASE_FRAGMENT,
                        TemplTypes.GO_DEFAULT_FRAGMENT,
                        TemplTypes.GO_FOR_START_FRAGMENT,
                        TemplTypes.DECL_GO_TOKEN
                    ).contains(baseLexer.tokenType)
                ) {
                    val emptyText = baseLexer.tokenSequence.toString().replace(Regex("\\S"), "")
                    modifications.addRangeToRemove(baseLexer.tokenStart, emptyText)
                    modifications.addOuterRange(
                        currentRange,
                        this.isInsertionToken(baseLexer.tokenType, baseLexer.tokenSequence)
                    )
                } else {
                    modifications.addOuterRange(
                        currentRange,
                        this.isInsertionToken(baseLexer.tokenType, baseLexer.tokenSequence)
                    )
                }
                baseLexer.advance()
            }

            return modifications
        }
    }

    private val goLanguageType = object : TemplateDataElementType("Go inside Templ", GoLanguage.INSTANCE, TemplTypes.GO_ROOT_FRAGMENT, TemplLeafElementType("TEMPL_NOT_GO")) {
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
                if (arrayOf(
                        TemplTypes.GO_ROOT_FRAGMENT,
                        TemplTypes.GO_IF_START_FRAGMENT,
                        TemplTypes.GO_ELSE_IF_START_FRAGMENT,
                        TemplTypes.GO_ELSE_START_FRAGMENT,
                        TemplTypes.GO_FRAGMENT,
                        TemplTypes.GO_SWITCH_START_FRAGMENT,
                        TemplTypes.GO_CASE_FRAGMENT,
                        TemplTypes.GO_DEFAULT_FRAGMENT,
                        TemplTypes.GO_FOR_START_FRAGMENT,
                        TemplTypes.DECL_GO_TOKEN
                    ).contains(baseLexer.tokenType)
                ) {
                    val tokenModifications =
                        this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                    modifications.addAll(tokenModifications)
                } else if (baseLexer.tokenType === TemplTypes.GO_EXPR) {
                    if (baseLexer.state == _TemplLexer.IN_EXPR) {
                        modifications.addRangeToRemove(baseLexer.tokenStart, "string(")
                        val tokenModifications =
                            this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                        modifications.addAll(tokenModifications)
                        modifications.addRangeToRemove(baseLexer.tokenEnd, ")")
                    } else {
                        modifications.addOuterRange(
                            currentRange,
                            this.isInsertionToken(baseLexer.tokenType, baseLexer.tokenSequence)
                        )
                    }
                } else if (baseLexer.tokenType === TemplTypes.HTML_DECL_START) {
                    modifications.addRangeToRemove(baseLexer.tokenStart, "func ")
                    modifications.addOuterRange(
                        currentRange,
                        this.isInsertionToken(baseLexer.tokenType, baseLexer.tokenSequence)
                    )
                } else if (baseLexer.tokenType === TemplTypes.LBRACE || baseLexer.tokenType === TemplTypes.RBRACE) {
                    if (arrayOf(
                            _TemplLexer.IN_TEMPL_DECLARATION_START,
                            _TemplLexer.IN_TEMPL_DECLARATION_BODY,
                            _TemplLexer.IN_EXPR,
                            _TemplLexer.IN_HTML_TAG_OPENER,
                        ).contains(baseLexer.state)) {
                            if (baseLexer.tokenType === TemplTypes.LBRACE && baseLexer.state == _TemplLexer.IN_TEMPL_DECLARATION_START) {
                                modifications.addRangeToRemove(baseLexer.tokenStart, "templ.Component ")
                            }
                            if (baseLexer.tokenType === TemplTypes.RBRACE && baseLexer.state == _TemplLexer.IN_TEMPL_DECLARATION_BODY) {
                                modifications.addRangeToRemove(baseLexer.tokenStart, "return nil")
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
                } else if (baseLexer.tokenType === TokenType.WHITE_SPACE) {
                    val tokenModifications =
                        this.appendCurrentTemplateToken(baseLexer.tokenEnd, baseLexer.tokenSequence)
                    modifications.addAll(tokenModifications)
                } else if (arrayOf(
                        TemplTypes.HTML_FRAGMENT,
                        TemplTypes.SCRIPT_BODY,
                    ).contains(baseLexer.tokenType)) {
                    val emptyText = baseLexer.tokenSequence.toString().replace(Regex("\\S"), "")
                    modifications.addRangeToRemove(baseLexer.tokenStart, emptyText)
                    modifications.addOuterRange(
                        currentRange,
                        this.isInsertionToken(baseLexer.tokenType, baseLexer.tokenSequence)
                    )
                } else {
                    modifications.addOuterRange(
                        currentRange,
                        this.isInsertionToken(baseLexer.tokenType, baseLexer.tokenSequence)
                    )
                }
                baseLexer.advance()
            }

            return modifications
        }
    }

    override fun getBaseLanguage(): Language {
        return TemplLanguage
    }

    override fun getLanguages(): Set<Language> {
        return setOf(baseLanguage, HTMLLanguage.INSTANCE, GoLanguage.INSTANCE)
    }

    override fun getTemplateDataLanguage(): Language {
        return HTMLLanguage.INSTANCE
    }

    override fun createFile(lang: Language): PsiFile? {
        val file = LanguageParserDefinitions.INSTANCE.forLanguage(lang).createFile(this) as PsiFileImpl
        when (lang) {
            HTMLLanguage.INSTANCE -> file.contentElementType = htmlElementType
            GoLanguage.INSTANCE -> file.contentElementType = goLanguageType
            TemplLanguage -> return file
            else -> return null
        }
        return file
    }

    override fun cloneInner(virtualFile: VirtualFile): MultiplePsiFilesPerDocumentFileViewProvider {
        return TemplFileViewProvider(manager, virtualFile, false)
    }
}
