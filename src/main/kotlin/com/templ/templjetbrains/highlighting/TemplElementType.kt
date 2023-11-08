package com.templ.templjetbrains.highlighting

import com.intellij.psi.tree.IElementType
import com.templ.templjetbrains.TemplLanguage
import org.jetbrains.plugins.textmate.language.syntax.lexer.TextMateScope;

class TemplElementType(private val scope: TextMateScope) : IElementType("TEMPL_TOKEN", TemplLanguage, false) {
    private val myScope = scope;

    fun getScope(): TextMateScope {
        return myScope;
    }

    override fun hashCode(): Int {
        return getScope().hashCode();
    }

    override fun toString(): String {
        return myScope.toString();
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true;
        if (other == null || this.javaClass != other.javaClass) return false;
        return (other as TemplElementType).getScope() == this.getScope();
    }
}