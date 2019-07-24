package org.zongf.plugins.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import org.zongf.plugins.idea.util.CodeTemplateUtil;
import org.zongf.plugins.idea.util.idea.EditorUtil;
import org.zongf.plugins.idea.util.idea.PsiClassUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

/** 优化基础方法: setter/getter/toString/Constructor
 * @author: zongf
 * @created: 2019-07-12
 * @since 1.0
 */
public class GenerateBasicMethodsAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        // 获取project, psiFile, editor
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        PsiFile psiFile =  anActionEvent.getData(PlatformDataKeys.PSI_FILE);
        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);

        // 校验文件: 打开的必须是java 文件
        if (editor == null || psiFile == null || !(psiFile instanceof PsiJavaFile)) {
            Messages.showErrorDialog("请打开java文件!", "文件类型错误");
            return;
        }

        // 获取光标所处于的类
        PsiClass psiClass = PsiClassUtil.getPsiClass((PsiJavaFile) psiFile, editor);

        if (psiClass == null) {
            PsiClass[] psiClasses = ((PsiJavaFile) psiFile).getClasses();
            if (psiClasses.length > 0) {
                psiClass = psiClasses[0];
            }
        }

        // 如果光标所在类不为空, 则优化当前类的所有基础方法
        if (psiClass != null) {
            clearBasicMethods(project, psiClass);
            generateBasicMethods(editor, psiClass);
        }
    }

    /** 清理基础方法
     * @param project
     * @param psiClass java类
     * @since 1.0
     * @author zongf
     * @created 2019-07-12 
     */
    public void clearBasicMethods(Project project, PsiClass psiClass) {

        // 获取基础方法列表
        List<PsiMethod> basicMethodList = new ArrayList<>();
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            if (isBasicMethod(psiMethod)) {
                basicMethodList.add(psiMethod);
            }
        }

        // 删除基础方法
        WriteCommandAction.runWriteCommandAction(project, () -> {
            basicMethodList.forEach(PsiElement::delete);
        });
    }

    /** 是否是基础方法: contructor, setter, getter, toString
     * @param psiMethod 待验证方法
     * @return true/false
     * @since 1.0
     * @author zongf
     * @created 2019-07-12 
     */
    private boolean isBasicMethod(PsiMethod psiMethod) {
        if (psiMethod.isConstructor() || isToStringMethod(psiMethod)
                || mayBeSetMethod(psiMethod) || mayBegetMethod(psiMethod)) {
            return true;
        }
        
        return false;
    }

    /** 判断是否是toString方法
     * @param psiMethod
     * @return true/false
     * @since 1.0
     * @author zongf
     * @created 2019-07-12
     */
    private boolean isToStringMethod(PsiMethod psiMethod) {
        if ("toString".equals(psiMethod.getName()) && psiMethod.getParameterList().isEmpty()) {
            return true;
        }
        return false;
    }

    /** 是否有可能为get方法. 满足条件: 以get或is开头, 紧跟大写字母开头的方法名, 可能为get方法
     * @param psiMethod 方法
     * @return 如果参数为空, 且方法名为setField 或 getField 格式,则返回为true. 否则返回false
     * @since 1.0
     * @author zongf
     * @created 2019-07-12
     */
    private boolean mayBegetMethod(PsiMethod psiMethod) {
        // 方法参数不为空, 则返回fasle;
        if (!psiMethod.getParameterList().isEmpty()) return false;

        return Pattern.compile("^(get|is)[A-Z].*").matcher(psiMethod.getName()).find();
    }

    /** 是否有可能为set方法. 满足条件: 参数只有一个, 方法名以set开头, 紧跟一个大写字母开头的方法, 可能为set方法
     * @param psiMethod 方法
     * @return 如果参数只有一个, 且方法名为setField ,则返回为true. 否则返回false
     * @since 1.0
     * @author zongf
     * @created 2019-07-12
     */
    private boolean mayBeSetMethod(PsiMethod psiMethod) {
        // 方法参数不为空, 则返回fasle;
        if (psiMethod.getParameterList().getParameters().length != 1) return false;

        return Pattern.compile("^set[A-Z].*").matcher(psiMethod.getName()).find();
    }

    /** 生成基础方法constructor, setter, getter, toString
     * @param editor 编辑器
     * @param psiClass java类
     * @since 1.0
     * @author zongf
     * @created 2019-07-12 
     */
    private void generateBasicMethods(Editor editor, PsiClass psiClass) {
        // 获取当前类的最后位置
        final int endOffset = psiClass.getTextRange().getEndOffset();

        // 获取当前类中所有字段
        LinkedHashMap<String, String> fieldMap = PsiClassUtil.getFields(psiClass);
        // 过滤serialVersionUID 字段
        fieldMap.keySet().forEach(key -> {if("serialVersionUID".equals(key)) fieldMap.remove(key);});

        // 获取自动生成代码
        String basicCode = CodeTemplateUtil.getBasicCode(psiClass.getName(), fieldMap);

        // 写入文件
        WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
            editor.getDocument().insertString(endOffset - 1, basicCode);
        });

        // 优化代码
        EditorUtil.optimizeMultiBlankLine(editor);
    }
}
