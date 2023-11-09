package com.templ.templ

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.LspServerSupportProvider.LspServerStarter
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

class TemplLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerStarter) {

        val templConfigService = TemplConfigService.getInstance(project)
        if (file.extension != "templ") return
        val executable =
                templConfigService.templLspExecutablePath?.let { File(it) }?.takeIf { it.exists() } ?: findGlobalTemplExecutable() ?: File("")
        serverStarter.ensureServerStarted(TemplLspServerDescriptor(project, executable))
    }
}
private class TemplLspServerDescriptor(project: Project, val executable: File) : ProjectWideLspServerDescriptor(project, "templ") {
    override fun isSupportedFile(file: VirtualFile) = file.extension == "templ"
    override fun createCommandLine() = GeneralCommandLine(executable.absolutePath, "lsp")
}