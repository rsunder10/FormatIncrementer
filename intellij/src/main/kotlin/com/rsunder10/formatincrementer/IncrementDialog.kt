package com.rsunder10.formatincrementer

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.DialogWrapper.DialogWrapperAction
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 * Inline pattern picker. Lets the user choose a [PatternType], edit the relevant
 * parameters, and see a live preview of the first generated values before applying.
 */
class IncrementDialog(
    private val project: Project?,
    initial: PatternConfig,
    private val caretCount: Int,
) : DialogWrapper(project) {

    private val typeCombo = ComboBox(PatternType.entries.toTypedArray()).apply {
        selectedItem = initial.type
    }

    private val startField = JBTextField(initial.start.toString())
    private val stepField = JBTextField(initial.step.toString())
    private val widthField = JBTextField(initial.width.toString())
    private val padCharField = JBTextField(initial.padChar.toString())
    private val radixField = JBTextField(initial.radix.toString())
    private val uppercaseBox = JBCheckBox("Uppercase", initial.uppercase)
    private val startDateField = JBTextField(initial.startDate)
    private val stepAmountField = JBTextField(initial.stepAmount.toString())
    private val stepUnitCombo = ComboBox(StepUnit.entries.toTypedArray()).apply {
        selectedItem = initial.stepUnit
    }
    private val dateFormatField = JBTextField(initial.dateFormat)
    private val templateField = JBTextField(initial.template)
    private val templateHint = JBLabel("Tokens: {n}, {n:03}, {n:hex}, {n:alpha}, {n:roman}").apply {
        foreground = JBUI.CurrentTheme.Label.disabledForeground()
    }

    private val previewLabel = JBLabel().apply { border = JBUI.Borders.emptyTop(8) }
    private val fieldsPanel = JPanel(BorderLayout())

    init {
        title = "Format Incrementer"
        setOKButtonText("Apply")
        init()
        wireListeners()
        rebuildFields()
        updatePreview()
    }

    override fun createLeftSideActions(): Array<javax.swing.Action> = arrayOf(
        object : DialogWrapperAction("Help") {
            override fun doAction(e: java.awt.event.ActionEvent?) {
                HelpDialogs.show(project, modal = false)
            }
        },
    )

    override fun createCenterPanel(): JComponent {
        val top = FormBuilder.createFormBuilder()
            .addLabeledComponent("Pattern:", typeCombo)
            .panel

        val root = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = JBUI.Borders.empty(10)
        }
        root.add(top)
        root.add(fieldsPanel)

        root.add(JBLabel("Applying to $caretCount caret(s)/selection(s).").apply {
            foreground = JBUI.CurrentTheme.Label.disabledForeground()
            border = JBUI.Borders.emptyTop(8)
        })
        root.add(previewLabel)
        root.preferredSize = JBUI.size(440, 240)
        return root
    }

    private fun rebuildFields() {
        val type = typeCombo.selectedItem as PatternType
        val fb = FormBuilder.createFormBuilder()
        when (type) {
            PatternType.NUMERIC -> fb
                .addLabeledComponent("Start:", startField)
                .addLabeledComponent("Step:", stepField)

            PatternType.PADDING -> fb
                .addLabeledComponent("Start:", startField)
                .addLabeledComponent("Step:", stepField)
                .addLabeledComponent("Width:", widthField)
                .addLabeledComponent("Pad char:", padCharField)

            PatternType.RADIX -> fb
                .addLabeledComponent("Start:", startField)
                .addLabeledComponent("Step:", stepField)
                .addLabeledComponent("Radix (2-36):", radixField)
                .addComponent(uppercaseBox)

            PatternType.ALPHA, PatternType.ROMAN -> fb
                .addLabeledComponent("Start:", startField)
                .addLabeledComponent("Step:", stepField)
                .addComponent(uppercaseBox)

            PatternType.DATES -> fb
                .addLabeledComponent("Start date:", startDateField)
                .addLabeledComponent("Step amount:", stepAmountField)
                .addLabeledComponent("Step unit:", stepUnitCombo)
                .addLabeledComponent("Date format:", dateFormatField)

            PatternType.TEMPLATE -> fb
                .addLabeledComponent("Start:", startField)
                .addLabeledComponent("Step:", stepField)
                .addLabeledComponent("Template:", templateField)
                .addComponent(templateHint)
        }
        fieldsPanel.removeAll()
        fieldsPanel.add(fb.panel, BorderLayout.NORTH)
        fieldsPanel.revalidate()
        fieldsPanel.repaint()
    }

    private fun wireListeners() {
        val docListener = object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = updatePreview()
            override fun removeUpdate(e: DocumentEvent) = updatePreview()
            override fun changedUpdate(e: DocumentEvent) = updatePreview()
        }
        listOf(
            startField, stepField, widthField, padCharField, radixField,
            startDateField, stepAmountField, dateFormatField, templateField,
        ).forEach { it.document.addDocumentListener(docListener) }

        uppercaseBox.addActionListener { updatePreview() }
        stepUnitCombo.addActionListener { updatePreview() }
        typeCombo.addActionListener {
            rebuildFields()
            updatePreview()
        }
    }

    private fun updatePreview() {
        val config = currentConfig()
        if (config == null) {
            previewLabel.text = "<html><b>Preview:</b> <i>invalid input</i></html>"
            return
        }
        val sample = Sequence.preview(config, minOf(6, maxOf(caretCount, 3)))
        previewLabel.text = "<html><b>Preview:</b> " + sample.joinToString(", ") + " ...</html>"
    }

    private fun currentConfig(): PatternConfig? {
        val type = typeCombo.selectedItem as PatternType
        val start = startField.text.trim().toLongOrNull()
        val step = stepField.text.trim().toLongOrNull()
        if (type != PatternType.DATES && (start == null || step == null)) return null
        val stepAmount = stepAmountField.text.trim().toLongOrNull()
        if (type == PatternType.DATES && stepAmount == null) return null
        return PatternConfig(
            type = type,
            start = start ?: PatternConfig().start,
            step = step ?: PatternConfig().step,
            width = widthField.text.trim().toIntOrNull() ?: PatternConfig().width,
            padChar = padCharField.text.firstOrNull() ?: '0',
            radix = radixField.text.trim().toIntOrNull() ?: PatternConfig().radix,
            uppercase = uppercaseBox.isSelected,
            startDate = startDateField.text.trim(),
            stepAmount = stepAmount ?: PatternConfig().stepAmount,
            stepUnit = stepUnitCombo.selectedItem as StepUnit,
            dateFormat = dateFormatField.text.trim(),
            template = templateField.text,
        )
    }

    override fun doValidate(): ValidationInfo? {
        val type = typeCombo.selectedItem as PatternType
        if (type != PatternType.DATES) {
            if (startField.text.trim().toLongOrNull() == null)
                return ValidationInfo("Start must be an integer", startField)
            if (stepField.text.trim().toLongOrNull() == null)
                return ValidationInfo("Step must be an integer", stepField)
        }
        if (type == PatternType.RADIX) {
            val r = radixField.text.trim().toIntOrNull()
            if (r == null || r < 2 || r > 36) return ValidationInfo("Radix must be 2-36", radixField)
        }
        if (type == PatternType.DATES && stepAmountField.text.trim().toLongOrNull() == null) {
            return ValidationInfo("Step amount must be an integer", stepAmountField)
        }
        return null
    }

    /** The config chosen by the user, valid only after the dialog is accepted. */
    val result: PatternConfig
        get() = currentConfig() ?: PatternConfig(type = typeCombo.selectedItem as PatternType)

    override fun getPreferredFocusedComponent(): JComponent = startField
}
