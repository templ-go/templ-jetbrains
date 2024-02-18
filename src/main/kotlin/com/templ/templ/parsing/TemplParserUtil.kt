package com.templ.templ.parsing

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.jetbrains.rd.generator.nova.PredefinedType

class TemplParserUtil: GeneratedParserUtilBase() {
    companion object {
        @JvmStatic
        fun parseTemplDecl(builder: PsiBuilder, level: Int, parse: (PsiBuilder, Int) -> Boolean): Boolean {
            assert(parse(builder, level))
            return true
        }
    }
}

