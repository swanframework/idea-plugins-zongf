package org.zongf.plugins.idea.util;

import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import org.zongf.plugins.idea.util.idea.EditorUtil;
import org.zongf.plugins.idea.util.idea.PsiClassUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author: zongf
 * @date: 2019-08-05 19:46
 */
public class MethodCommentUtil {

    /** 生成方法注释
     * @param templatePath 模板名称
     * @author: zongf
     * @time: 2019-08-05 19:53:18
     */
    public static void writeMethodComment(Editor editor, PsiFile psiFile, String templatePath, boolean focusSecondLine) {

        // 获取当前所在方法
        PsiMethod currentMethod = MethodCommentUtil.getCursorInMethod(editor, psiFile);

        // 如果当前光标所在方法为null, 则不进行任何操作
        if(currentMethod == null) return;


        // 生成注释
        WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {

            // 如果文档注释不为空, 则先删除现有文档注释
            PsiDocComment docComment = currentMethod.getDocComment();
            if (docComment != null)  docComment.delete();

            // 获取注释内容
            String content = MethodCommentUtil.getMethodComment(editor, currentMethod, templatePath);
            // 获取方法第一行行首偏移量
            int methodLineStartOffset = EditorUtil.getLineOffsetStart(editor, currentMethod.getTextRange().getStartOffset());
            editor.getDocument().insertString(methodLineStartOffset -1, content);

            // 获取第一行行尾偏移量
            int lineOffsetEnd = EditorUtil.getLineOffsetEnd(editor, methodLineStartOffset);

            // 聚焦注释第二行, 默认聚焦注释第一行
            if (focusSecondLine) {
                lineOffsetEnd = EditorUtil.getLineOffsetEnd(editor, lineOffsetEnd + 1);
            }

            editor.getCaretModel().moveToOffset(lineOffsetEnd);
        });

    }

    /** 获取方法注释内容
     * @return: null
     * @author: zongf
     * @time: 2019-08-05 19:48:38
     */
    private static String getMethodComment(Editor editor, PsiMethod currentMethod, String templatePath) {
        // 解析参数
        List<String> paramList = MethodCommentUtil.getParameterNames(currentMethod);

        // 返回值
        String result = currentMethod.getReturnType().getCanonicalText();

        // 获取方法开始行的行首空白字符
        String indent = MethodCommentUtil.getMethodIndentBeforeStartLine(editor, currentMethod);

        // 获取注释模板
        return CodeTemplateUtil.getAddMethodComment(paramList, result, indent, templatePath);
    }

    /** 获取方法首行(方法签名行)缩进字符串
     * @return: 方法签名行缩进字符串
     * @author: zongf
     * @time: 2019-08-05 19:27:50
     */
    private static String getMethodIndentBeforeStartLine(Editor editor, PsiMethod method) {

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
    private static List<String> getParameterNames(PsiMethod method) {
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
    private static PsiMethod getCursorInMethod(Editor editor, PsiFile psiFile) {

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
