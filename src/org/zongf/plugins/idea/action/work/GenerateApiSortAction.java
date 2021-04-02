package org.zongf.plugins.idea.action.work;

import com.intellij.jam.JamNumberAttributeElement;
import com.intellij.jam.JamStringAttributeElement;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiImplUtil;
import com.intellij.psi.impl.source.tree.java.PsiAnnotationImpl;
import com.intellij.psi.impl.source.tree.java.PsiExpressionListImpl;
import com.intellij.psi.impl.source.tree.java.PsiJavaTokenImpl;
import com.intellij.psi.ref.AnnotationAttributeChildLink;
import com.intellij.psi.tree.java.IJavaElementType;
import org.zongf.plugins.idea.util.MethodCommentUtil;

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
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(anActionEvent.getProject());
        for (PsiClass aClass : classes) {

            PsiMethod[] methods = aClass.getMethods();

            int sort = 100;
            for (PsiMethod method : methods) {
                PsiAnnotation annotation = method.getAnnotation("io.swagger.annotations.ApiOperationSort");

                AnnotationAttributeChildLink link = new AnnotationAttributeChildLink("value");
                WriteCommandAction.runWriteCommandAction(annotation.getProject(), () -> {
//                    addAttribute(annotation, "100", link);
                    chg(annotation, "100", link);
                });

            }
        }

    }


    protected static PsiAnnotationMemberValue addAttribute(PsiAnnotation annotation, String valueText, final AnnotationAttributeChildLink link) {
        final PsiElementFactory factory = JavaPsiFacade.getElementFactory(annotation.getProject());
        PsiAnnotationMemberValue literal = factory.createExpressionFromText(valueText, null);

        PsiAnnotationMemberValue attr = link.findLinkedChild(annotation);
        if (attr == null) {
            literal = (PsiAnnotationMemberValue)link.createChild(annotation).replace(literal);
        } else if (attr instanceof PsiArrayInitializerMemberValue) {
            literal = (PsiAnnotationMemberValue) attr.add(literal);
        } else {

            PsiAnnotationMemberValue arrayInit = factory.createAnnotationFromText("@ApiOperationSort({})", null).findDeclaredAttributeValue(null);
            arrayInit.add(attr);
            arrayInit = annotation.setDeclaredAttributeValue(link.getAttributeName(), arrayInit);
            literal = (PsiAnnotationMemberValue)arrayInit.add(literal);
        }
        return literal;
    }

    public static void chg(PsiAnnotation annotation, String value, final AnnotationAttributeChildLink attributeLink) {
        PsiAnnotationMemberValue existing = annotation.findAttributeValue("io.swagger.annotations.ApiOperationSort");

        PsiAnnotationSupport support = LanguageAnnotationSupport.INSTANCE.forLanguage(annotation.getLanguage());
        assert support != null;


        PsiLiteral valueElement = (PsiLiteral)JavaPsiFacade.getElementFactory(annotation.getProject()).createExpressionFromText(StringUtil.escapeStringCharacters(value) , (PsiElement)null);

        annotation.setDeclaredAttributeValue("order", valueElement);

        if (attributeLink != null) {
            annotation.setDeclaredAttributeValue(attributeLink.getAttributeName(), valueElement);
        } else {
            if (valueElement != null) {
                existing.replace(valueElement);
            } else {
                final PsiElement parent = existing.getParent();
                (parent instanceof PsiNameValuePair ? parent : existing).delete();
            }
        }
    }
}
