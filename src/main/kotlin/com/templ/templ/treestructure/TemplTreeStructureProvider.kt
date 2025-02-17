package com.templ.templ.treestructure

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.NestingTreeNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.templ.templ.TemplFileType

fun getGeneratedPath(path: String): String {
    return path.removeSuffix(".templ")+"_templ.go"
}
fun getTxtPath(path: String): String {
    return path.removeSuffix(".templ")+"_templ.txt"
}

class TemplTreeStructureProvider : TreeStructureProvider {
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: MutableCollection<AbstractTreeNode<*>>,
        settings: ViewSettings?
    ): MutableCollection<AbstractTreeNode<*>> {
        // Find all the templ nodes and map them to their generated file paths
        val templNodes = mutableMapOf<String, AbstractTreeNode<*>>()
        val templToGeneratedFiles = mutableMapOf<AbstractTreeNode<*>, MutableList<PsiFileNode>>()

        // First pass: identify templ files
        for (child in children) {
            if (child is PsiFileNode) {
                val file = child.virtualFile
                if (file != null && !file.isDirectory && file.fileType is TemplFileType) {
                    templNodes[getGeneratedPath(file.path)] = child
                    templNodes[getTxtPath(file.path)] = child
                    templToGeneratedFiles[child] = mutableListOf()
                }
            }
        }

        // Process all files
        val nodes = ArrayList<AbstractTreeNode<*>>()
        for (child in children) {
            if (child is PsiFileNode) {
                val file = child.virtualFile
                val templFile = templNodes[file?.path]
                if (file != null && templFile != null) {
                    // Add this generated file to the list of files to nest under the templ file
                    templToGeneratedFiles[templFile]?.add(child)
                    continue
                }
                if (file?.fileType is TemplFileType) {
                    // Skip templ files for now, they will be added with their nested files
                    continue
                }
            }
            nodes.add(child)
        }

        // Add templ files with their nested generated files
        for (templFile in templToGeneratedFiles.keys) {
            val generatedFiles = templToGeneratedFiles[templFile]
            if (generatedFiles != null && generatedFiles.isNotEmpty()) {
                nodes.add(NestingTreeNode(templFile as PsiFileNode, generatedFiles))
            } else {
                nodes.add(templFile)
            }
        }

        return nodes
    }
}