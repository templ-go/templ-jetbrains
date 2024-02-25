package com.templ.templ

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object TemplFileType : LanguageFileType(TemplLanguage) {
    override fun getName(): String = "templ"

    override fun getDescription(): String = "A language for writing HTML user interfaces in Go. "

    override fun getDefaultExtension(): String = "templ"

    override fun getIcon(): Icon = IconLoader.getIcon("/templ.svg", this::class.java)
}
