package com.frees.ideaplugindependencyfolding.model

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

/**
 * Custom node type for grouped external libraries in the Project View.
 * This node represents a group of external libraries (either by source or package).
 */
class DependencyGroupNode(
    project: Project,
    viewSettings: ViewSettings,
    private val groupName: String,
    private val children: List<AbstractTreeNode<*>>,
    private val groupIcon: Icon? = null
) : ProjectViewNode<String>(project, groupName, viewSettings) {

    override fun getChildren(): Collection<AbstractTreeNode<*>> = children

    override fun update(presentation: PresentationData) {
        presentation.presentableText = groupName
        if (groupIcon != null) {
            presentation.setIcon(groupIcon)
        }
    }

    override fun contains(file: VirtualFile): Boolean {
        return children.any { child -> 
            when (child) {
                is ProjectViewNode<*> -> child.contains(file)
                else -> false
            }
        }
    }

    // getValue is final in the parent class, so we don't override it
    // The value is set in the constructor

    override fun canNavigate(): Boolean = false

    override fun canNavigateToSource(): Boolean = false

    override fun toString(): String = groupName
}
