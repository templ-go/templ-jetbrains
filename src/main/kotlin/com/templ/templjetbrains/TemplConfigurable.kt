package com.templ.templjetbrains

import com.intellij.codeInsight.template.impl.TemplateEditorUtil
import com.intellij.json.JsonLanguage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.EditorSettings
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.NlsContexts.ConfigurableName
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory
import com.intellij.ui.components.JBCheckBox
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.SwingHelper
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class TemplConfigurable(private val project: Project) : Configurable {

    private val myTemplLspPath = TextFieldWithBrowseButton()

    override fun getDisplayName(): @ConfigurableName String {
        return "Templ"
    }

    override fun createComponent(): JComponent {
        val service = TemplSettings.getService(project)
        myTemplLspPath.text = service.getTemplLspPath()

        val mainFormBuilder = FormBuilder.createFormBuilder()
        mainFormBuilder.addLabeledComponent("LSP Path", myTemplLspPath)
        val wrapper = JPanel(BorderLayout())
        wrapper.add(mainFormBuilder.panel, BorderLayout.NORTH)
        return wrapper
    }

    override fun isModified(): Boolean {
        val service = TemplSettings.getService(project)
        return myTemplLspPath.text != service.getTemplLspPath()
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        if (isModified) {
            val service = TemplSettings.getService(project)
            service.setTemplLspPath(myTemplLspPath.text)
        }
    }
}