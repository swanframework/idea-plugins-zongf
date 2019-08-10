package org.zongf.plugins.idea.action.mvn;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import org.zongf.plugins.idea.ui.MvnSearchDialog;

/** maven 依赖搜索
 * @since 1.0
 * @author zongf
 * @created 2019-08-10
 */
public class MvnSearchAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);

        MvnSearchDialog dialog = new MvnSearchDialog(editor);
        dialog.setVisible(true);
    }
}
