package org.zongf.plugins.idea.action;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;

/** 代码格式化工具
 * @author: zongf
 * @created: 2019-07-09
 * @since 1.0
 */
public class CodeFormatAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        // 获取当前类信息
        PsiFile psiFile = anActionEvent.getData(PlatformDataKeys.PSI_FILE);
        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);

        // 如果文件为空
        if (psiFile == null) {
            Messages.showInfoMessage("请打开要生成格式化的文件","未选中合适文件");
            return;
        }

        // 格式化代码
        codeFormat(psiFile, editor);
    }


    /** 格式化代码. 如果未选中任何代码, 则格式化整个文件
     * @param psiFile 需要格式化的文件
     * @param editor 编辑器
     * @since 1.0
     * @author zongf
     * @created 2019-07-09
     */
    public static void codeFormat(PsiFile psiFile, Editor editor) {
        WriteCommandAction.runWriteCommandAction(psiFile.getProject(),()->{

            // 未选中任何文档
            if (editor.getSelectionModel().getSelectionStart() == editor.getSelectionModel().getSelectionEnd()) {
                // 选中所有文档
                editor.getSelectionModel().setSelection(0,  editor.getDocument().getTextLength());

                // 格式化选中内容
                new ReformatCodeProcessor(psiFile, editor.getSelectionModel()).runWithoutProgress();

                //取消选中所有文档
                editor.getSelectionModel().removeSelection();
            }else {
                // 格式化选中内容
                new ReformatCodeProcessor(psiFile, editor.getSelectionModel()).runWithoutProgress();
            }
        });
    }

}
