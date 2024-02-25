package com.templ.templ.file

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.templ.templ.TemplFileType
import com.templ.templ.TemplLanguage

class TemplFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, TemplLanguage) {
    override fun getFileType(): FileType {
        return TemplFileType
    }

    override fun toString(): String {
        return "Templ File"
    }
}
