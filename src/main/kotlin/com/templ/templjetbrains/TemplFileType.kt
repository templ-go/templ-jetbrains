package com.templ.templjetbrains

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.fileTypes.PlainTextFileType
import javax.swing.Icon

object TemplFileType : LanguageFileType(TemplLanguage) {
    override fun getName(): String = "templ"

    override fun getDescription(): String = "A language for writing HTML user interfaces in Go. "

    override fun getDefaultExtension(): String = "templ"

    override fun getIcon(): Icon = PlainTextFileType.INSTANCE.getIcon()
}