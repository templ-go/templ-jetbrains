package com.templ.templjetbrains

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.lang.Language
import java.io.File

object TemplLanguage : Language("templ") {
    override fun getDisplayName(): String = "templ"
}

fun findGlobalTemplExecutable(): File? =
        PathEnvironmentVariableUtil.findInPath("templ")