package com.templ.templ.file

import com.intellij.lang.Language
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.lang.html.HTMLLanguage
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.PsiFileImpl
import com.intellij.psi.templateLanguages.TemplateDataElementType
import com.intellij.psi.templateLanguages.TemplateDataModifications
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider
import com.templ.templ.TemplLanguage
import com.templ.templ.psi.TemplLeafElementType
import com.templ.templ.psi.TemplTypes

class TemplFileViewProvider(manager: PsiManager, virtualFile: VirtualFile, eventSystemEnabled: Boolean) :
    MultiplePsiFilesPerDocumentFileViewProvider(manager, virtualFile, eventSystemEnabled), TemplateLanguageFileViewProvider {

    val htmlElementType = object : TemplateDataElementType("HTML inside Templ", HTMLLanguage.INSTANCE, TemplTypes.HTML_FRAGMENT, TemplLeafElementType("TEMPL_NOT_HTML")) {
        override fun appendCurrentTemplateToken(tokenEndOffset: Int, tokenText: CharSequence): TemplateDataModifications {
            // Detect if were inside an attribute value and if so, insert fake quotes to make the HTML parser happy.
            if (StringUtil.endsWithChar(tokenText, '=')) {
                return TemplateDataModifications.fromRangeToRemove(tokenEndOffset, "\"\"");
            }

            return super.appendCurrentTemplateToken(tokenEndOffset, tokenText);
        }
    }

    override fun getBaseLanguage(): Language {
        return TemplLanguage
    }

    override fun getLanguages(): Set<Language> {
        return setOf(getBaseLanguage(), HTMLLanguage.INSTANCE)
    }

    override fun getTemplateDataLanguage(): Language {
        return HTMLLanguage.INSTANCE
    }

    override fun createFile(lang: Language): PsiFile? {
        if (lang === HTMLLanguage.INSTANCE) {
            val file = LanguageParserDefinitions.INSTANCE.forLanguage(lang).createFile(this) as PsiFileImpl
            file.contentElementType = htmlElementType
            return file
        } else if (lang === getBaseLanguage()) {
            return LanguageParserDefinitions.INSTANCE.forLanguage(lang).createFile(this) as PsiFileImpl
        } else {
            return null
        }
    }

    override fun cloneInner(virtualFile: VirtualFile): MultiplePsiFilesPerDocumentFileViewProvider {
        return TemplFileViewProvider(manager, virtualFile, false)
    }
}
