package com.rsunder10.formatincrementer

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAware

/** Opens the Format Incrementer help guide. */
class HelpAction : AnAction(), DumbAware {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        HelpDialogs.show(e.project, modal = true)
    }
}

/** Shared entry point for opening help from the menu or from inside another dialog. */
object HelpDialogs {

    fun show(project: com.intellij.openapi.project.Project?, modal: Boolean = true) {
        HelpDialog(project, modal).show()
    }
}
