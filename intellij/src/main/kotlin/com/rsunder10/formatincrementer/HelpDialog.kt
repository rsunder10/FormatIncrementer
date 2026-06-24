package com.rsunder10.formatincrementer

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.event.HyperlinkEvent
import javax.swing.text.html.HTMLEditorKit

/**
 * Scrollable in-IDE help guide for Format Incrementer.
 *
 * @param modal When false, the dialog can be shown on top of the pattern picker without
 *   being blocked by its modality.
 */
class HelpDialog(
    project: Project?,
    private val modal: Boolean = true,
) : DialogWrapper(project) {

    init {
        title = "Format Incrementer Help"
        isModal = modal
        init()
    }

    override fun createCenterPanel(): JComponent {
        val pane = JEditorPane().apply {
            editorKit = HTMLEditorKit()
            contentType = "text/html"
            isEditable = false
            putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true)
            addHyperlinkListener { e ->
                if (e.eventType == HyperlinkEvent.EventType.ACTIVATED && e.url != null) {
                    com.intellij.ide.BrowserUtil.browse(e.url)
                }
            }
            text = HelpContent.html()
            caretPosition = 0
        }
        return JBScrollPane(pane).apply {
            preferredSize = Dimension(JBUI.scale(560), JBUI.scale(520))
            border = JBUI.Borders.empty(8)
        }
    }

    override fun createActions(): Array<javax.swing.Action> = arrayOf(okAction)
}
