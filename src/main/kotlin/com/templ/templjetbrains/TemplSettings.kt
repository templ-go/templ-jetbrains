package com.templ.templjetbrains

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

class TemplState {
    var templLspPath = ""
}

@State(name = "TemplSettings", storages = [Storage("templ.xml")])
class TemplSettings(private val project: Project) : PersistentStateComponent<TemplState> {
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

    fun setTemplLspPath(path: String) {
        this.state.templLspPath = path
    }
}