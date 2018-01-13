import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;

import java.util.concurrent.atomic.AtomicInteger;

public class Incrementer extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();
        final SelectionModel selectionModel = editor.getSelectionModel();

        CaretModel caretModel = editor.getCaretModel();
        AtomicInteger index = new AtomicInteger();
        caretModel.runForEachCaret(x -> {
            final int start = selectionModel.getSelectionStart();
            final int end = selectionModel.getSelectionEnd();
            int i = index.getAndIncrement() + 1;
            Runnable runnable = () -> {
                document.replaceString(start, end, String.valueOf(i));
            };
            WriteCommandAction.runWriteCommandAction(project, runnable);
            selectionModel.removeSelection();
        });
    }
}

