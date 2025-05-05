package com.frees.ideaplugindependencyfolding.model

import com.frees.ideaplugindependencyfolding.settings.DependencyFoldingSettings
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import javax.swing.Icon

/**
 * Logic for package-based grouping of external libraries.
 */
class DependencyPackageGroup(
    private val project: Project,
    private val viewSettings: ViewSettings
) {

    /**
     * Groups the given nodes by their package structure.
     */
    fun groupByPackage(nodes: List<AbstractTreeNode<*>>): List<AbstractTreeNode<*>> {
        if (nodes.isEmpty()) return emptyList()

        // Group nodes by package
        val packageGroups = mutableMapOf<String, MutableList<AbstractTreeNode<*>>>()

        for (node in nodes) {
            val packageName = identifyPackage(node)
            packageGroups.getOrPut(packageName) { mutableListOf() }.add(node)
        }

        // Create group nodes
        return packageGroups.map { (packageName, children) ->
            DependencyGroupNode(
                project,
                viewSettings,
                packageName,
                children,
                getIconForPackage(packageName)
            )
        }
    }

    /**
     * Identifies the package of an external library node.
     */
    private fun identifyPackage(node: AbstractTreeNode<*>): String {
        val nodeName = node.name ?: return "Other"

        // Try to match with common prefixes from settings
        val settings = DependencyFoldingSettings.getInstance()
        for (mapping in settings.commonPrefixes) {
            if (nodeName.contains(mapping.prefix, ignoreCase = true)) {
                return mapping.displayName
            }
        }

        // Extract package from node name
        val packagePattern = Regex("([a-z][a-z0-9_]*(\\.[a-z0-9_]+)+[0-9a-z_])", RegexOption.IGNORE_CASE)
        val match = packagePattern.find(nodeName)

        if (match != null) {
            val fullPackage = match.groupValues[1]
            // Take just the first segment of the package
            val segments = fullPackage.split(".")
            return if (segments.isNotEmpty()) {
                segments[0]
            } else {
                fullPackage
            }
        }

        // If no package is found, use a default group
        return "Other"
    }

    /**
     * Returns an icon for the given package.
     */
    private fun getIconForPackage(packageName: String): Icon? {
        // In a real implementation, we would return specific icons for each package
        // For now, we'll return null and let the default icon be used
        return null
    }
}
