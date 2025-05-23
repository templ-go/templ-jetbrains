package com.templ.templ

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

class TemplState {
    var templLspPath = ""
    var goplsLog = ""
    var goplsRPCTrace = false
    var log = ""
    var pprof = false
    var http = ""
    var noPreload = false
    var goplsRemote = ""
}

@State(name = "TemplSettings", storages = [Storage("templ.xml")])
class TemplSettings : PersistentStateComponent<TemplState> {
    companion object {
        fun getService(project: Project): TemplSettings = project.service<TemplSettings>()
    }

    private var state = TemplState()

    override fun getState(): TemplState {
        return state
    }

    override fun loadState(state: TemplState) {
        this.state = state
    }

    fun getTemplLspPath(): String {
        val templPath = this.state.templLspPath
        if (templPath.isEmpty()) {
            return findGlobalTemplExecutable()?.absolutePath ?: ""
        }
        return templPath
    }
}
