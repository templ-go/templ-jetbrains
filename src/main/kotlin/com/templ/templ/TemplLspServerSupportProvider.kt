package com.templ.templ

import com.goide.sdk.GoSdkService
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.LspServerSupportProvider.LspServerStarter
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class TemplLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerStarter) {

        val templConfigService = TemplSettings.getService(project)
        if (file.extension != "templ") return
        val executable = File(templConfigService.getTemplLspPath())
        if (!executable.exists()) return
        serverStarter.ensureServerStarted(TemplLspServerDescriptor(project, executable))
    }
}

private class TemplLspServerDescriptor(project: Project, val executable: File) :
    ProjectWideLspServerDescriptor(project, "templ") {
    override fun isSupportedFile(file: VirtualFile) = file.extension == "templ"
    override fun createCommandLine(): GeneralCommandLine {
        val cmd = GeneralCommandLine(executable.absolutePath, "lsp")
        val settings = TemplSettings.getService(project).state
        if (settings.goplsLog.isNotEmpty()) cmd.addParameter("-goplsLog=${settings.goplsLog}")
        if (settings.log.isNotEmpty()) cmd.addParameter("-log=${settings.log}")
        if (settings.http.isNotEmpty()) cmd.addParameter("-http=${settings.http}")
        if (settings.goplsRPCTrace) cmd.addParameter("-goplsRPCTrace=true")
        if (settings.pprof) cmd.addParameter("-pprof=true")
        if (settings.noPreload) cmd.addParameter("-no-preload=true")
        if (settings.goplsRemote.isNotEmpty()) cmd.addParameter("-gopls-remote=${settings.goplsRemote}")

        val goPath = GoSdkService.getInstance(project).getSdk(null).executable?.parent?.path
        val currentPath = System.getenv("PATH").orEmpty()
        goPath?.let { cmd.withEnvironment("PATH", "$it:$currentPath") }

        return cmd
    }
}

fun findGlobalTemplExecutable(): File? {
    PathEnvironmentVariableUtil.findExecutableInPathOnAnyOS("templ")?.let { return it }
    for (loc in templLocations)
        if (Files.exists(loc))
            return loc.toFile()
    return null
}

private val templLocations = arrayOf(
    System.getenv("GOBIN")?.let { Paths.get(it, "templ") },
    System.getenv("GOBIN")?.let { Paths.get(it, "templ.exe") },
    System.getenv("GOPATH")?.let { Paths.get(it, "bin", "templ") },
    System.getenv("GOPATH")?.let { Paths.get(it, "bin", "templ.exe") },
    System.getenv("GOROOT")?.let { Paths.get(it, "bin", "templ") },
    System.getenv("GOROOT")?.let { Paths.get(it, "bin", "templ.exe") },
    System.getenv("HOME")?.let { Paths.get(it, "bin", "templ") },
    System.getenv("HOME")?.let { Paths.get(it, "bin", "templ.exe") },
    System.getenv("HOME")?.let { Paths.get(it, "go", "bin", "templ") },
    System.getenv("HOME")?.let { Paths.get(it, "go", "bin", "templ.exe") },
    Paths.get("/usr/local/bin/templ"),
    Paths.get("/usr/bin/templ"),
    Paths.get("/usr/local/go/bin/templ"),
    Paths.get("/usr/local/share/go/bin/templ"),
    Paths.get("/usr/share/go/bin/templ"),
).filterNotNull()
