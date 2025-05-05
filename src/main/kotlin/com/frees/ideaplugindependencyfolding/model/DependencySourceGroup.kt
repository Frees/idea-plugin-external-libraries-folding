package com.frees.ideaplugindependencyfolding.model

import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.ui.IconManager
import javax.swing.Icon

/**
 * Logic for identifying and grouping dependencies by source (Maven, Gradle, etc.).
 */
class DependencySourceGroup(
    private val project: Project,
    private val viewSettings: ViewSettings
) {
    companion object {
        // Common source types
        const val MAVEN_SOURCE = "Maven"
        const val GRADLE_SOURCE = "Gradle"
        const val NPM_SOURCE = "NPM"
        const val UNKNOWN_SOURCE = "Other"

        // Patterns to identify source types
        private val MAVEN_PATTERN = Regex("Maven: .*")
        private val GRADLE_PATTERN = Regex("Gradle: .*")
        private val NPM_PATTERN = Regex("NPM: .*")
    }

    /**
     * Groups the given nodes by their dependency source.
     */
    fun groupBySource(nodes: List<AbstractTreeNode<*>>): List<AbstractTreeNode<*>> {
        if (nodes.isEmpty()) return emptyList()

        // Group nodes by source
        val sourceGroups = mutableMapOf<String, MutableList<AbstractTreeNode<*>>>()

        for (node in nodes) {
            val source = identifySource(node)
            sourceGroups.getOrPut(source) { mutableListOf() }.add(node)
        }

        // Create group nodes
        return sourceGroups.map { (source, children) ->
            DependencyGroupNode(
                project,
                viewSettings,
                source,
                children,
                getIconForSource(source)
            )
        }
    }

    /**
     * Identifies the source of a dependency node.
     */
    private fun identifySource(node: AbstractTreeNode<*>): String {
        val path = node.name ?: return UNKNOWN_SOURCE
        //
        return when {
            MAVEN_PATTERN.matches(path) -> MAVEN_SOURCE
            GRADLE_PATTERN.matches(path) -> GRADLE_SOURCE
            NPM_PATTERN.matches(path) -> NPM_SOURCE
            else -> UNKNOWN_SOURCE
        }
    }

    /**
     * Returns an icon for the given source type.
     */
    private fun getIconForSource(source: String): Icon? {
        // In a real implementation, we would return specific icons for each source type
        // For now, we'll return null and let the default icon be used
        return null
    }
}
