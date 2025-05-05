package com.frees.ideaplugindependencyfolding.view

import com.frees.ideaplugindependencyfolding.model.DependencyGroupNode
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.ui.SimpleTextAttributes

/**
 * Custom ProjectViewNodeDecorator implementation for customizing node appearance in the Project View.
 * This decorator is responsible for customizing the appearance of dependency group nodes.
 */
class DependencyFoldingProjectViewNodeDecorator : ProjectViewNodeDecorator {

    /**
     * Customizes the presentation of a node in the Project View.
     */
    override fun decorate(node: ProjectViewNode<*>, data: PresentationData) {
        // We only need to customize DependencyGroupNode instances
        if (node is DependencyGroupNode) {
            // We could customize the presentation further here if needed
            // For example, add a tooltip or change the text color
            data.tooltip = "External Lib group: ${node.value}"

            // Make the group name bold to distinguish it from regular dependencies
            data.addText(node.value, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
        }
    }
}
