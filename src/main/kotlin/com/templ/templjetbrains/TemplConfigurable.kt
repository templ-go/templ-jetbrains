package com.templ.templjetbrains

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent


class TemplConfigurable internal constructor(project: Project) : Configurable {
    private val templConfigService: TemplConfigService = TemplConfigService.getInstance(project)
    private val configPanel: TemplConfigPanel = TemplConfigPanel(project)
    override fun getDisplayName(): String {
        return "Templ"
    }

    override fun getHelpTopic(): String? {
        return null
    }

    override fun createComponent(): JComponent {
        reset()
        return configPanel.configPanel
    }

    override fun reset() {}

    override fun isModified(): Boolean {
        return templConfigService.globalTemplLspExecutablePath != configPanel.globalTemplLspExecutablePath

    }

    override fun apply() {
        templConfigService.globalTemplLspExecutablePath = configPanel.globalTemplLspExecutablePath
    }

    override fun disposeUIResources() {
    }
}