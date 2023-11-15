package com.templ.templ

import com.intellij.lang.Language

object TemplLanguage : Language("templ") {
    private fun readResolve(): Any = TemplLanguage
    override fun getDisplayName(): String = "templ"
}
