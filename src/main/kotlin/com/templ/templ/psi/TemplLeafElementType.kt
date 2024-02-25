package com.templ.templ.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.templateLanguages.OuterLanguageElementImpl
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.ILeafElementType
import com.templ.templ.TemplLanguage

class TemplLeafElementType(debugName: String): IElementType(debugName, TemplLanguage), ILeafElementType {
    override fun createLeafNode(charSequence: CharSequence): ASTNode {
        return OuterLanguageElementImpl(this, charSequence)
    }
}
