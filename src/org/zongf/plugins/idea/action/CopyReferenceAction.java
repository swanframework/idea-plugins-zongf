package org.zongf.plugins.idea.action;

import com.intellij.ide.actions.QualifiedNameProvider;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.psi.*;
import org.zongf.plugins.idea.util.idea.ClipBoardUtil;

/** 复制引用至剪切板, 使用全限定类名
 * @author: zongf
 * @created: 2019-07-11
 * @since 1.0
 */
public class CopyReferenceAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        // 获取PsiFile 和 selection
        PsiFile psiFile = anActionEvent.getData(PlatformDataKeys.PSI_FILE);

        // 如果未打开文件, 则返回
        if (anActionEvent.getData(PlatformDataKeys.EDITOR) == null) {
            return;
        }

        SelectionModel selection = anActionEvent.getData(PlatformDataKeys.EDITOR).getSelectionModel();

        // 获取选中元素
        PsiElement selectElement = psiFile.findElementAt(selection.getSelectionStart());

        // 获取行号
        int lineNum = selection.getSelectionStartPosition().getLine() + 1;

        // 获取唯一名称
        String qualifiedName = null;
        QualifiedNameProvider[] providers = (QualifiedNameProvider[])Extensions.getExtensions(QualifiedNameProvider.EP_NAME);
        for (QualifiedNameProvider provider : providers) {
            qualifiedName = provider.getQualifiedName(selectElement);
            if(qualifiedName != null) break;
        }

        // 如果唯一名称为空, 则返回类名
        if (qualifiedName == null) {
            String relativePath = psiFile.getVirtualFile().getCanonicalPath().replace(psiFile.getProject().getBasePath() + "/","");
            qualifiedName = relativePath + "/" + psiFile.getName();
        }

        // 拼接字符串
        String info = qualifiedName + ":" + lineNum;

        // 粘贴到剪切板
        ClipBoardUtil.setStringContent(info);
    }
}
