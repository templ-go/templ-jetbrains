package com.templ.templ

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.NlsContexts.ConfigurableName
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class TemplConfigurable(private val project: Project) : Configurable {

    private val myTemplLspPath = TextFieldWithBrowseButton()
    private val goplsLog = TextFieldWithBrowseButton()
    private val log = TextFieldWithBrowseButton()
    private val goplsRPCTrace = JCheckBox()
    private val pprof = JCheckBox()
    private val http = JTextField()


    override fun getDisplayName(): @ConfigurableName String {
        return "Templ"
    }

    override fun createComponent(): JComponent {
        val service = TemplSettings.getService(project)
        myTemplLspPath.text = service.getTemplLspPath()
        goplsLog.text = service.state.goplsLog
        log.text = service.state.log
        goplsRPCTrace.isSelected = service.state.goplsRPCTrace
        pprof.isSelected = service.state.pprof
        http.text = service.state.http

        val mainFormBuilder = FormBuilder.createFormBuilder()
        mainFormBuilder.addLabeledComponent("LSP Path", myTemplLspPath)
        mainFormBuilder.addLabeledComponent("goplsLog", goplsLog)
        mainFormBuilder.addLabeledComponent("log", log)
        mainFormBuilder.addLabeledComponent("goplsRPCTrace", goplsRPCTrace)
        mainFormBuilder.addLabeledComponent("pprof", pprof)
        mainFormBuilder.addLabeledComponent("http", http)
        val wrapper = JPanel(BorderLayout())
        wrapper.add(mainFormBuilder.panel, BorderLayout.NORTH)
        return wrapper
    }

    override fun isModified(): Boolean {
        val service = TemplSettings.getService(project)
        val state = service.state
        return myTemplLspPath.text != state.templLspPath ||
                goplsLog.text != state.goplsLog ||
                log.text != state.log ||
                goplsRPCTrace.isSelected != state.goplsRPCTrace ||
                pprof.isSelected != state.pprof ||
                http.text != state.http

    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        if (isModified) {
            val service = TemplSettings.getService(project)
            val state = service.state
            state.log = log.text
            state.goplsLog = goplsLog.text
            state.goplsRPCTrace = goplsRPCTrace.isSelected
            state.http = http.text
            state.templLspPath = myTemplLspPath.text
            service.loadState(state)
        }
    }
}