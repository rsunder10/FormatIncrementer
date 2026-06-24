package com.rsunder10.formatincrementer

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.event.HyperlinkEvent

/** Scrollable in-IDE help guide for Format Incrementer. */
class HelpDialog(project: Project?) : DialogWrapper(project) {

    init {
        title = "Format Incrementer Help"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val pane = JEditorPane("text/html", HelpContent.html()).apply {
            isEditable = false
            putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true)
            addHyperlinkListener { e ->
                if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                    com.intellij.ide.BrowserUtil.browse(e.url)
                }
            }
        }
        return JBScrollPane(pane).apply {
            preferredSize = Dimension(JBUI.scale(560), JBUI.scale(520))
            border = JBUI.Borders.empty(8)
        }
    }

    override fun createActions(): Array<javax.swing.Action> = arrayOf(okAction)
}
