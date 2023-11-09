package com.templ.templ.spellchecker

import com.intellij.spellchecker.BundledDictionaryProvider

class TemplBundledDictionaryProvider : BundledDictionaryProvider {
    override fun getBundledDictionaries(): Array<String> {

        return arrayOf(
            "/templ.dic",
        )
    }
}