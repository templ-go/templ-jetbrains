package com.templ.templ

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.SystemDependent

@State(name = "TemplConfigService", storages = [Storage("templ.xml")])
@Service(Service.Level.PROJECT)
class TemplConfigService : PersistentStateComponent<TemplConfigService> {
    var globalTemplLspExecutablePath: @SystemDependent String? = null

    override fun getState(): TemplConfigService {
        return this
    }

    override fun loadState(config: TemplConfigService) {
        XmlSerializerUtil.copyBean(config, this)
    }

    val templLspExecutablePath: @SystemDependent String?
        get() {
            return globalTemplLspExecutablePath
        }
    companion object {
        fun getInstance(project: Project): TemplConfigService {
            return project.getService(TemplConfigService::class.java)
        }
    }

}