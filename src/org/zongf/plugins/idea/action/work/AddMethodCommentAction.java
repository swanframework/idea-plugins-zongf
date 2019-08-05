package org.zongf.plugins.idea.action.work;

import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.zongf.plugins.idea.util.CodeTemplateUtil;
import org.zongf.plugins.idea.util.idea.EditorUtil;
import org.zongf.plugins.idea.util.idea.PsiClassUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author: zongf
 * @date: 2019-08-05 14:49
 */
public class AddMethodCommentAction extends AnAction {
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

        // 获取当前所在方法
        PsiMethod currentMethod = getCursorInMethod(editor, psiFile);

        // 如果当前光标所在方法为null, 则不进行任何操作
        if(currentMethod == null) return;

        // 解析参数
        List<String> paramList = getParameterNames(currentMethod);

        // 返回值
        String result = currentMethod.getReturnType().getCanonicalText();

        // 获取方法开始行的行首空白字符
        String indent = getMethodIndentBeforeStartLine(editor, currentMethod);

        // 获取注释模板
        String content = CodeTemplateUtil.getAddMethodComment(paramList, result, indent);

        // 生成注释
        WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
            int methodLineStartOffset = EditorUtil.getLineOffsetStart(editor, currentMethod.getTextRange().getStartOffset());
            editor.getDocument().insertString(methodLineStartOffset -1, content);
        });
    }

    /** 获取方法首行(方法签名行)缩进字符串
     * @return: 方法签名行缩进字符串
     * @author: zongf
     * @time: 2019-08-05 19:27:50
     */
    private String getMethodIndentBeforeStartLine(Editor editor, PsiMethod method) {

        // 获取方法开始处的偏移量
        int methodStartOffset = method.getTextRange().getStartOffset();

        // 获取方法首行，行首偏移量
        int methodLineStartOffset = EditorUtil.getLineOffsetStart(editor, methodStartOffset);

        return editor.getDocument().getText(new TextRange(methodLineStartOffset, methodStartOffset));
    }


    /** 获取java形参列表
     * @param method java 方法
     * @return: List<String> java形参列表
     * @author: zongf
     * @time: 2019-08-05 19:22:06
     */
    private List<String> getParameterNames(PsiMethod method) {
        List<String> paramList = new ArrayList<>();
        // 获取方法的形参列表
        for (JvmParameter jvmParameter : method.getParameters()) {
            paramList.add(jvmParameter.getName());
        }
        return paramList;
    }

    /**
     * @Description: 获取光标所在方法
     * @return: PsiMethod
     * @author: zongf
     * @time: 2019-08-05 19:05:31
     */
    private PsiMethod getCursorInMethod(Editor editor, PsiFile psiFile) {

        // 获取光标所处于的类
        PsiClass psiClass = PsiClassUtil.getPsiClass((PsiJavaFile) psiFile, editor);

        // 获取光标开始处偏移量
        int selectionStart = editor.getSelectionModel().getSelectionStart();

        for (PsiMethod method : psiClass.getMethods()) {
            TextRange textRange = method.getTextRange();
            // 如果光标开始处在方法的偏移量区间内，则表示在该方法中.
            if (textRange.getStartOffset() < selectionStart && textRange.getEndOffset() > selectionStart) {
                return method;
            }
        }
        return null;
    }
}
