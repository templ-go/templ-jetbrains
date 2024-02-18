package com.templ.templ.parsing

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.templ.templ.TemplLanguage
import com.templ.templ.file.TemplFile
import com.templ.templ.psi.TemplTokenSets
import com.templ.templ.psi.TemplTokenType
import com.templ.templ.psi.TemplTypes

class TemplParserDefinition: ParserDefinition {
    val FILE = IFileElementType(TemplLanguage);

    override fun createLexer(project: Project?): Lexer {
        return TemplLexer()
    }

    override fun createParser(project: Project?): PsiParser {
        return TemplParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun getCommentTokens(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun createElement(node: ASTNode?): PsiElement {
        return TemplTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return TemplFile(viewProvider)
    }
}
