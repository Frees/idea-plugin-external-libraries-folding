package com.frees.ideaplugindependencyfolding.model

import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import javax.swing.Icon

/**
 * Logic for package-based grouping of dependencies.
 */
class DependencyPackageGroup(
    private val project: Project,
    private val viewSettings: ViewSettings
) {
    companion object {
        // Common package prefixes
        private val COMMON_PREFIXES = listOf(
            "org.springframework" to "Spring",
            "org.apache" to "Apache",
            "com.google" to "Google",
            "io.netty" to "Netty",
            "org.jetbrains" to "JetBrains",
            "com.fasterxml" to "FasterXML",
            "org.slf4j" to "SLF4J",
            "org.junit" to "JUnit",
            "javax" to "Java EE",
            "jakarta" to "Jakarta EE",
            "com.squareup" to "Square",
            "com.github" to "GitHub",
            "org.hibernate" to "Hibernate",
            "org.mockito" to "Mockito",
            "org.eclipse" to "Eclipse",
            "org.gradle" to "Gradle",
            "org.scala-lang" to "Scala",
            "org.apache.commons" to "Apache Commons",
            "org.bouncycastle" to "Bouncy Castle",
            "org.mapstruct" to "MapStruct",
            "org.thymeleaf" to "Thymeleaf",
            "org.yaml" to "YAML",
            "org.dom4j" to "Dom4j",
            "org.hamcrest" to "Hamcrest",
            "org.glassfish" to "GlassFish",
            "org.jboss" to "JBoss",
            "org.springframework.boot" to "Spring Boot",
            "org.springframework.cloud" to "Spring Cloud",
            "org.springframework.data" to "Spring Data",
            "org.springframework.security" to "Spring Security",
            "org.springframework.web" to "Spring Web"
        )
    }

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
     * Identifies the package of a dependency node.
     */
    private fun identifyPackage(node: AbstractTreeNode<*>): String {
        val nodeName = node.name ?: return "Other"

        // Try to match with common prefixes first
        for ((prefix, name) in COMMON_PREFIXES) {
            if (nodeName.contains(prefix, ignoreCase = true)) {
                return name
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
