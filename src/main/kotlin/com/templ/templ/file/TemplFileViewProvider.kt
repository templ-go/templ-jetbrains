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
import com.templ.templ.file.languages.GoLanguageType
import com.templ.templ.file.languages.HtmlElementType
import com.templ.templ.parsing._TemplLexer
import com.templ.templ.psi.TemplLeafElementType
import com.templ.templ.psi.TemplTypes
import org.jetbrains.annotations.NotNull

class TemplFileViewProvider(manager: PsiManager, virtualFile: VirtualFile, eventSystemEnabled: Boolean) :
    MultiplePsiFilesPerDocumentFileViewProvider(manager, virtualFile, eventSystemEnabled), TemplateLanguageFileViewProvider {

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
            HTMLLanguage.INSTANCE -> file.contentElementType = HtmlElementType()
            GoLanguage.INSTANCE -> file.contentElementType = GoLanguageType()
            TemplLanguage -> return file
            else -> return null
        }
        return file
    }

    override fun cloneInner(virtualFile: VirtualFile): MultiplePsiFilesPerDocumentFileViewProvider {
        return TemplFileViewProvider(manager, virtualFile, false)
    }
}
