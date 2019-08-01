package org.zongf.plugins.idea.action.work;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import org.zongf.plugins.idea.util.common.UnicodeStringUtil;

public class UnicodeFileConverterAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);

        // 如果文件为空
        if (editor == null) {
            Messages.showInfoMessage("请打开文本文件!","未选中合适文件");
            return;
        }

        // 获取编辑器内容
        String text = editor.getDocument().getText();

        // 转换字符串
        String convertStr = UnicodeStringUtil.unicode2String(text);

        // 重新写入编辑器
        WriteCommandAction.runWriteCommandAction(editor.getProject(), ()->{
            editor.getDocument().setText(convertStr);
        });

    }

}
