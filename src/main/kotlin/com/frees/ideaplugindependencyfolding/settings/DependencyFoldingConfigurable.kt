package com.frees.ideaplugindependencyfolding.settings

import com.intellij.openapi.options.Configurable
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.table.JBTable
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.table.AbstractTableModel
import java.awt.BorderLayout
import javax.swing.BoxLayout

/**
 * Configurable component for the External Lib Folding plugin settings.
 */
class DependencyFoldingConfigurable : Configurable {
    private var settingsComponent: DependencyFoldingSettingsComponent? = null

    override fun getDisplayName(): String = "External Lib Folding"

    override fun createComponent(): JComponent {
        settingsComponent = DependencyFoldingSettingsComponent()
        return settingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings = DependencyFoldingSettings.getInstance()
        val component = settingsComponent ?: return false
        return component.prefixMappings != settings.commonPrefixes ||
               component.foldingEnabled != settings.foldingEnabled
    }

    override fun apply() {
        val settings = DependencyFoldingSettings.getInstance()
        val component = settingsComponent ?: return
        settings.commonPrefixes = component.prefixMappings
        settings.foldingEnabled = component.foldingEnabled
    }

    override fun reset() {
        val settings = DependencyFoldingSettings.getInstance()
        settingsComponent?.reset()
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}

/**
 * UI component for editing the prefix mappings.
 */
class DependencyFoldingSettingsComponent {
    var prefixMappings: MutableList<PrefixMapping> = mutableListOf()
    var foldingEnabled: Boolean = true
    val panel: JPanel = JPanel(BorderLayout())

    private val tableModel = PrefixMappingsTableModel()
    private val table = JBTable(tableModel)
    private val enableFoldingCheckbox = JBCheckBox("Enable external lib folding", true)

    init {
        val topPanel = JPanel()
        topPanel.layout = BoxLayout(topPanel, BoxLayout.Y_AXIS)
        topPanel.add(enableFoldingCheckbox)

        // Add listener to the checkbox
        enableFoldingCheckbox.addActionListener {
            foldingEnabled = enableFoldingCheckbox.isSelected
        }

        val toolbarDecorator = ToolbarDecorator.createDecorator(table)
            .setAddAction { addMapping() }
            .setRemoveAction { removeSelectedMappings() }
            .disableUpDownActions()

        panel.add(topPanel, BorderLayout.NORTH)
        panel.add(toolbarDecorator.createPanel(), BorderLayout.CENTER)
        reset()
    }

    private fun addMapping() {
        prefixMappings.add(PrefixMapping("", ""))
        tableModel.fireTableDataChanged()
    }

    private fun removeSelectedMappings() {
        val selectedRows = table.selectedRows
        val toRemove = selectedRows.map { prefixMappings[it] }.toList()
        prefixMappings.removeAll(toRemove)
        tableModel.fireTableDataChanged()
    }

    fun reset() {
        val settings = DependencyFoldingSettings.getInstance()
        prefixMappings = settings.commonPrefixes.toMutableList()
        foldingEnabled = settings.foldingEnabled
        enableFoldingCheckbox.isSelected = foldingEnabled
        tableModel.fireTableDataChanged()
    }

    inner class PrefixMappingsTableModel : AbstractTableModel() {
        private val columnNames = arrayOf("Package Prefix", "Display Name")

        override fun getRowCount(): Int = prefixMappings.size

        override fun getColumnCount(): Int = 2

        override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
            val mapping = prefixMappings[rowIndex]
            return when (columnIndex) {
                0 -> mapping.prefix
                1 -> mapping.displayName
                else -> ""
            }
        }

        override fun getColumnName(column: Int): String = columnNames[column]

        override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = true

        override fun setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int) {
            val mapping = prefixMappings[rowIndex]
            when (columnIndex) {
                0 -> mapping.prefix = aValue.toString()
                1 -> mapping.displayName = aValue.toString()
            }
            fireTableCellUpdated(rowIndex, columnIndex)
        }
    }
}
