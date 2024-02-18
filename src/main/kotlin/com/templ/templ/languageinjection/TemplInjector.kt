package com.templ.templ.languageinjection

import com.goide.GoLanguage
import com.goide.psi.GoFile
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.xml.XmlText
import com.templ.templ.psi.TemplGoRoot
import com.templ.templ.psi.impl.TemplGoRootImpl

// FIXME: Language injection requires much more work. The plan would be to inject Go, Javascript and CSS.
// Best place to check is Jetbrains opensource repositories for MultiHostInjector references documentation
// https://plugins.jetbrains.com/docs/intellij/language-injection.html#multihostinjector
// https://github.com/JetBrains/intellij-plugins
// https://github.com/JetBrains/intellij-community
class TemplInjector: MultiHostInjector {
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (context is TemplGoRootImpl) {
            registrar
                .startInjecting(GoLanguage.INSTANCE)
                .addPlace(
                    null, null,
                    context as PsiLanguageInjectionHost,
                    context.getTextRange()
                )
                .doneInjecting()
        }
    }

    override fun elementsToInjectIn(): MutableList<out Class<out PsiElement>> {
        return mutableListOf(TemplGoRootImpl::class.java)
    }
}
