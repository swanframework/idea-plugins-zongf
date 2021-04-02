package org.zongf.plugins.idea.action.work;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.impl.source.tree.java.PsiAnnotationImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description:
 * @author: zongf
 * @date: 2019-08-05 14:49
 */
public class GenerateApiSortAction extends AnAction {
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

        PsiClass[] classes = ((PsiJavaFile) psiFile).getClasses();

        String annoName = "io.swagger.annotations.ApiOperationSort";

        WriteCommandAction.runWriteCommandAction(anActionEvent.getProject(), () -> {
            for (PsiClass aClass : classes) {
                // 智能导入依赖
                addImport(psiFile.getProject(), aClass, StringUtils.substringAfterLast(annoName, "."));

                final AtomicInteger sort = new AtomicInteger(1);
                for (PsiMethod method : aClass.getMethods()) {

                    PsiAnnotation annotation = method.getAnnotation(annoName);

                    // 如果没有注解添加注解
                    if (annotation == null) {
                        addAnnotation(method, annoName);
                        annotation = method.getAnnotation(annoName);
                    }

                    setAttribute(annotation, "value", sort.getAndIncrement());
                }
            }
        });

    }

    private void addImport(Project project, PsiClass psiClass, String simpleClassName) {

        PsiElementFactory psiElementFactory = JavaPsiFacade.getInstance(project).getElementFactory();

        GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);

        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(simpleClassName, searchScope);

        for (PsiClass importCl : psiClasses) {
            PsiImportStatement importStatement = psiElementFactory.createImportStatement(importCl);

            ((PsiJavaFile) psiClass.getContainingFile()).getImportList().add(importStatement);
        }


    }

    private void addAnnotation(PsiMethod method, String annoName) {
        method.getModifierList().addAnnotation(annoName);
    }

    /** 新增或更新属性值
     * @param annotation 注解
     * @param attributeName 属性名
     * @param value 值
     * @author zongf
     * @date 2021-04-02
     */
    public static void setAttribute(PsiAnnotation annotation, String attributeName, Integer value) {
        PsiLiteral valueElement = (PsiLiteral)JavaPsiFacade.getElementFactory(annotation.getProject())
                .createExpressionFromText(StringUtil.escapeStringCharacters(String.valueOf(value)) , null);
        annotation.setDeclaredAttributeValue(attributeName, valueElement);
    }
}
