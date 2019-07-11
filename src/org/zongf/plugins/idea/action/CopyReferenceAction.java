package org.zongf.plugins.idea.action;

import com.intellij.ide.actions.QualifiedNameProvider;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
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

        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);

        // 如果未打开文件, 则返回
        if (editor == null) {
            return;
        }

        SelectionModel selection = editor.getSelectionModel();

        // 获取选中元素
        PsiElement selectElement = psiFile.findElementAt(selection.getSelectionStart());

        // 获取真实行号, 纵使编辑器折叠也没事
        int lineNum = editor.getDocument().getLineNumber(selection.getSelectionStart()) + 1;

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
        }else {
            // 如果为java方法时, 如果不包含括号, 说明方法唯一. 默认方法唯一时, 方法签名不包含形参列表, 自己拼接形参列表
            if (psiFile instanceof PsiJavaFile && selectElement.getParent() instanceof PsiMethod) {
                if(!qualifiedName.contains("(")){
                    qualifiedName = qualifiedName + getUniqueMethodParams((PsiMethod) selectElement.getParent());
                }
            }
        }

        // 拼接字符串
        String info = qualifiedName + ":" + lineNum;

        // 粘贴到剪切板
        ClipBoardUtil.setStringContent(info);
    }

    /** 获取唯一方法参数
     * @param psiMethod java 方法
     * @return String 参数列表
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    private static String getUniqueMethodParams(PsiMethod psiMethod) {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        for (PsiParameter psiParameter : psiMethod.getParameterList().getParameters()) {
            sb.append(psiParameter.getType().getCanonicalText()).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(")");
        return sb.toString();
    }
}
