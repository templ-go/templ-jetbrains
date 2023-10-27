package com.templ.templjetbrains

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
                templConfigService.templLspExecutablePath?.let { File(it) }?.takeIf { it.exists() } ?: detectTemplExecutable(
                        project, templConfigService, true
                ) ?: return
        serverStarter.ensureServerStarted(TemplLspServerDescriptor(project, executable))

    }
}
fun detectTemplExecutable(project: Project, templConfigService: TemplConfigService, lsp: Boolean): File? {
    if (templConfigService.templLspExecutablePath != null) return File(templConfigService.templLspExecutablePath)
   return null
}
private class TemplLspServerDescriptor(project: Project, val executable: File) : ProjectWideLspServerDescriptor(project, "templ") {
    override fun isSupportedFile(file: VirtualFile) = file.extension == "templ"
    override fun createCommandLine() = GeneralCommandLine(executable.absolutePath, "lsp")
}