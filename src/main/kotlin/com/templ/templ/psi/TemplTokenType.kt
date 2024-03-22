package com.templ.templ.psi

import com.intellij.psi.tree.IElementType
import com.templ.templ.TemplLanguage

class TemplTokenType(debugName: String) : IElementType(debugName, TemplLanguage) {
    override fun toString(): String {
        return "TemplTokenType." + super.toString()
    }
}
