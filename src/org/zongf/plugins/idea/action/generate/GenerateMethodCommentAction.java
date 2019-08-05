package org.zongf.plugins.idea.action.generate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.zongf.plugins.idea.util.MethodCommentUtil;

/**
 * @Description:
 * @author: zongf
 * @date: 2019-08-05 14:49
 */
public class GenerateMethodCommentAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        // 获取project, psiFile, editor
        PsiFile psiFile =  anActionEvent.getData(PlatformDataKeys.PSI_FILE);
        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);

        // 校验文件: 打开的必须是java 文件
        if (editor == null || psiFile == null || !(psiFile instanceof PsiJavaFile)) {
            Messages.showErrorDialog("请打开java文件!", "文件类型错误");
            return;
        }

        // 生成注释
        MethodCommentUtil.writeMethodComment(editor, psiFile, "generateMethodComments.ftl");
    }

}
