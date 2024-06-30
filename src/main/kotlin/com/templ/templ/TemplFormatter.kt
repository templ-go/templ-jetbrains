package com.templ.templ

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessAdapter
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.formatting.FormattingContext
import com.intellij.formatting.service.AsyncDocumentFormattingService
import com.intellij.formatting.service.AsyncFormattingRequest
import com.intellij.formatting.service.FormattingService
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import java.io.File
import java.nio.charset.StandardCharsets


class TemplFormatter : AsyncDocumentFormattingService() {
    override fun getFeatures(): MutableSet<FormattingService.Feature> {
        return mutableSetOf(FormattingService.Feature.AD_HOC_FORMATTING)
    }

    override fun canFormat(psi: PsiFile): Boolean {
        return psi.fileType == TemplFileType
    }

    override fun createFormattingTask(request: AsyncFormattingRequest): FormattingTask? {
        val formattingContext: FormattingContext = request.context
        val project = formattingContext.project
        val file = request.ioFile ?: return null

        val templConfigService = TemplSettings.getService(project)
        if (file.extension != "templ") return null
        val executable = File(templConfigService.getTemplLspPath())
        if (!executable.exists()) return null

        val params = SmartList<String>()

        params.add("fmt")
        params.add("-stdout")
        params.add(file.absolutePath)

        val commandLine: GeneralCommandLine = GeneralCommandLine()
            .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
            .withExePath(executable.absolutePath)
            .withParameters(params)

        val handler = OSProcessHandler(commandLine.withCharset(StandardCharsets.UTF_8))

        return object : FormattingTask {
            override fun run() {
                handler.addProcessListener(object : CapturingProcessAdapter() {
                    override fun processTerminated(event: ProcessEvent) {
                        val exitCode = event.exitCode
                        if (exitCode == 0) {
                            request.onTextReady(output.stdout)
                        } else {
                            request.onError("TemplFormatter", output.stderr)
                        }
                    }
                })

                handler.startNotify()
            }

            override fun cancel(): Boolean {
                handler.destroyProcess()
                return true
            }
        }
    }

    override fun getNotificationGroupId() = "TemplFormatter"

    override fun getName() = "TemplFormatter"
}
