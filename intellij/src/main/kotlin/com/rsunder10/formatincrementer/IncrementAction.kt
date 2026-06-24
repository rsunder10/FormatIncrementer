package com.rsunder10.formatincrementer

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project

/**
 * Fills every caret/selection in the editor with a value from a user-chosen sequence.
 */
class IncrementAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val project = e.project ?: run {
            e.presentation.isEnabled = false
            return
        }
        e.presentation.isEnabled = resolveEditor(project, e) != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = resolveEditor(project, e) ?: return
        val document = editor.document

        val carets: List<Caret> = editor.caretModel.allCarets.sortedBy { it.offset }

        val dialog = IncrementDialog(project, ConfigStore.load(), carets.size)
        if (!dialog.showAndGet()) return

        val config = dialog.result
        ConfigStore.save(config)

        data class Edit(val start: Int, val end: Int, val text: String)

        val edits = carets.mapIndexed { i, caret ->
            val start = if (caret.hasSelection()) caret.selectionStart else caret.offset
            val end = if (caret.hasSelection()) caret.selectionEnd else caret.offset
            Edit(start, end, Sequence.render(config, i))
        }.sortedByDescending { it.start }

        WriteCommandAction.runWriteCommandAction(project, "Format Incrementer", null, {
            edits.forEach { document.replaceString(it.start, it.end, it.text) }
        })

        editor.caretModel.allCarets.forEach { it.removeSelection() }
    }

    /**
     * Keyboard shortcuts often omit [CommonDataKeys.EDITOR] from the data context during
     * [update], which would leave the action disabled and swallow the shortcut.
     */
    private fun resolveEditor(project: Project, e: AnActionEvent): Editor? =
        e.getData(CommonDataKeys.EDITOR)
            ?: FileEditorManager.getInstance(project).selectedTextEditor
}
