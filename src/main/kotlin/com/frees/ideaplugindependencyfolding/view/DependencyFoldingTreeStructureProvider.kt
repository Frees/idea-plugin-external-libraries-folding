package com.frees.ideaplugindependencyfolding.view

import com.frees.ideaplugindependencyfolding.model.DependencyGroupNode
import com.frees.ideaplugindependencyfolding.model.DependencyPackageGroup
import com.frees.ideaplugindependencyfolding.model.DependencySourceGroup
import com.frees.ideaplugindependencyfolding.settings.DependencyFoldingSettings
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project

/**
 * Custom TreeStructureProvider implementation for modifying the Project View tree structure.
 * This provider is responsible for transforming the flat External Libraries list into a hierarchical tree structure.
 */
class DependencyFoldingTreeStructureProvider(private val project: Project) : TreeStructureProvider {

    companion object {
        // The name of the External Libraries node in the Project View
        private const val EXTERNAL_LIBRARIES_NODE = "External Libraries"
    }

    /**
     * Modifies the children of a node in the Project View.
     * This method is called for each node in the tree when it's being expanded.
     */
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: Collection<AbstractTreeNode<*>>,
        settings: ViewSettings
    ): Collection<AbstractTreeNode<*>> {
        // Check if folding is enabled in settings
        if (!DependencyFoldingSettings.getInstance().foldingEnabled) {
            return children
        }

        // We only want to modify the External Libraries node
        val parentValue = parent.value.toString()

        // Use a more flexible approach to identify the External Libraries node
        if (parentValue.contains(EXTERNAL_LIBRARIES_NODE, ignoreCase = true)) {
            return organizeExternalLibraries(children, settings)
        }

        return children
    }

    /**
     * Organizes the External Libraries node by grouping dependencies.
     */
    private fun organizeExternalLibraries(
        children: Collection<AbstractTreeNode<*>>,
        settings: ViewSettings
    ): Collection<AbstractTreeNode<*>> {
        if (children.isEmpty()) return children

        val childrenList = children.toList()

        // First, group by source (Maven, Gradle, etc.)
        val sourceGroup = DependencySourceGroup(project, settings)
        val sourceGrouped = sourceGroup.groupBySource(childrenList)

        // Then, for each source group, group by package
        val packageGroup = DependencyPackageGroup(project, settings)
        val result = mutableListOf<AbstractTreeNode<*>>()

        for (node in sourceGrouped) {
            val nodeChildren = node.children.toList()
            if (nodeChildren.isNotEmpty()) {
                // If this is a source group with children, group its children by package
                val packageGrouped = packageGroup.groupByPackage(nodeChildren)
                result.add(node.copy(packageGrouped))
            } else {
                // If this is a leaf node or an empty group, add it as is
                result.add(node)
            }
        }

        return result
    }

    /**
     * Helper method to create a copy of a node with new children.
     */
    private fun AbstractTreeNode<*>.copy(newChildren: Collection<AbstractTreeNode<*>>): AbstractTreeNode<*> {
        val children = this.children.toList()
        if (children == newChildren) return this

        // Create a new DependencyGroupNode with the same properties but different children
        if (this is DependencyGroupNode) {
            return DependencyGroupNode(
                project,
                this.settings,
                this.value,
                newChildren.toList(),
                null // We don't have access to the original icon, so use null
            )
        }

        // For other node types, return the original node
        return this
    }
}
