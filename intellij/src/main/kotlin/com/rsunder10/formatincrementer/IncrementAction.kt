package com.rsunder10.formatincrementer

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Caret

/**
 * Fills every caret/selection in the editor with a value from a user-chosen sequence.
 *
 * Replaces the legacy auto-increment-only behavior: the user now picks a pattern in
 * [IncrementDialog], and all carets are updated inside a single undoable command.
 */
class IncrementAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null &&
            e.getData(CommonDataKeys.PROJECT) != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        val document = editor.document

        // Order carets by document position so the sequence follows reading order.
        val carets: List<Caret> = editor.caretModel.allCarets.sortedBy { it.offset }

        val dialog = IncrementDialog(project, ConfigStore.load(), carets.size)
        if (!dialog.showAndGet()) return

        val config = dialog.result
        ConfigStore.save(config)

        // Compute replacements first, then apply from last to first so earlier offsets
        // remain valid as the document length changes.
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
}
