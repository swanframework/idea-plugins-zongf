package org.zongf.plugins.idea.util.idea;

import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author zongf
 * @date 2020-03-18
 */
public class PsiFileUtil {

    /** 打开文件
     * @param project  项目
     * @param virtualFile 文件名
     * @param lineNumber 行号
     * @author zongf
     * @date 2020-03-18
     */
    public static void openFile(Project project, VirtualFile virtualFile, int lineNumber) {
        // 打开文件
        OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, virtualFile);
        Editor editor = FileEditorManager.getInstance(project).openTextEditor(openFileDescriptor, true);

        if(lineNumber == -1) lineNumber = 0;

        // 跳转到指定行
        CaretModel caretModel = editor.getCaretModel();
        LogicalPosition logicalPosition = caretModel.getLogicalPosition();
        logicalPosition.leanForward(true);

        if (lineNumber == -1) {
            // 定位到行
            LogicalPosition logical = new LogicalPosition(0 , logicalPosition.column);
            caretModel.moveToLogicalPosition(logical);
        }else {
            // 定位到行
            LogicalPosition logical = new LogicalPosition(lineNumber , logicalPosition.column);
            caretModel.moveToLogicalPosition(logical);
            // 选中行
            SelectionModel selectionModel = editor.getSelectionModel();
            selectionModel.selectLineAtCaret();
        }


    }
}
