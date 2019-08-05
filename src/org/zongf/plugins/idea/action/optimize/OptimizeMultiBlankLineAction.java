package org.zongf.plugins.idea.action.optimize;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import org.zongf.plugins.idea.util.idea.EditorUtil;

/** 优化多行空白行
 * @author: zongf
 * @created: 2019-07-17
 * @since 1.0
 */
public class OptimizeMultiBlankLineAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);

        if (editor == null) {
            Messages.showErrorDialog("请打开文件!", "文件选择错误");
        }

        EditorUtil.optimizeMultiBlankLine(editor);
    }
}
