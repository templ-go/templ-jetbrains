package com.templ.templ.file

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.FileViewProviderFactory
import com.intellij.psi.PsiManager
import com.templ.templ.TemplLanguage

class TemplFileViewProviderFactory: FileViewProviderFactory {
    override fun createFileViewProvider(virtualFile: VirtualFile, language: Language?, psiManager: PsiManager, eventSystemEnabled: Boolean): FileViewProvider {
        assert(language!!.isKindOf(TemplLanguage))
        return TemplFileViewProvider(psiManager, virtualFile, eventSystemEnabled)
    }
}
