package com.templ.templjetbrains

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBTextField
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.emptyText
import com.templ.templjetbrains.TemplConfigService.Companion.getInstance
import org.jetbrains.annotations.SystemDependent
import java.io.File

import javax.swing.JPanel


class TemplConfigPanel(project: Project) {
    lateinit var configPanel: JPanel
    private lateinit var globalTemplLspExecutablePathField: TextFieldWithBrowseButton
    init {
        val templConfigService = getInstance(project)

        globalTemplLspExecutablePathField.apply {
            addBrowseFolderListener(null, null, null, FileChooserDescriptorFactory.createSingleFileDescriptor())
            if (textField is JBTextField) {
                when (val globalTemplLspExecutablePath = templConfigService.globalTemplLspExecutablePath?.takeIf { File(it).exists() }) {
                    is String -> textField.text = globalTemplLspExecutablePath
                    else -> setAutodetectedTemplLsp()
                }
            }
            textField.isEditable = false
        }


    }


    private fun setAutodetectedTemplLsp() =
            when (val templLspExecutablePath = findGlobalTemplExecutable()?.absolutePath) {
                is String -> globalTemplLspExecutablePathField.text = templLspExecutablePath
                else -> {
                    globalTemplLspExecutablePathField.text = ""
                    globalTemplLspExecutablePathField.emptyText.text = TEMPL_LSP_EXECUTABLE_NOT_FOUND
                }
            }


    val globalTemplLspExecutablePath: @SystemDependent String?
        get() = globalTemplLspExecutablePathField.text.takeIf { it.isNotBlank() }

    companion object {
        const val TEMPL_LSP_EXECUTABLE_NOT_FOUND =  "templ executable not found"
    }
}