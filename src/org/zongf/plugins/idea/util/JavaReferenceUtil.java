package org.zongf.plugins.idea.util;

import com.intellij.ide.actions.JavaQualifiedNameProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.zongf.plugins.idea.util.common.StringUtil;
import org.zongf.plugins.idea.vo.JavaReferenceVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author: zongf
 * @created: 2019-07-11
 * @since 1.0
 */
public class JavaReferenceUtil {

    /** 将java引用对象转换为String字符串
     * @param javaReferenceVO java 引用对象
     * @return function 字符串处理方式
     * @since 1.0
     * @author zongf
     * @created 2019-07-11 
     */
    public static String buildReference(JavaReferenceVO javaReferenceVO, Function<String, String> function) {

        StringBuffer sb = new StringBuffer();

        // 拼接类名
        sb.append(function.apply(javaReferenceVO.getClassName()));

        // 判断是否是方法
        if (javaReferenceVO.getMethodName() != null) { //选中区域是方法

            // 拼接方法名
            sb.append(".").append(javaReferenceVO.getMethodName());

            // 拼接参数
            sb.append("(");
            for (String param : javaReferenceVO.getParamList()) {
                sb.append(function.apply(param)).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append(")");
        }else { // 选中区域不是方法
            if (javaReferenceVO.getCodeLine() != null) {
                sb.append("#").append(javaReferenceVO.getCodeLine().trim());
            }
        }

        // 拼接行号
        sb.append(" :").append(javaReferenceVO.getLineNo());
        return sb.toString();
    }

    /** 将选中文字转换为java应用对象
     * @param psiJavaFile java 文件
     * @param editor java文件编辑器
     * @return java 引用对象
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    public static JavaReferenceVO parser(PsiJavaFile psiJavaFile, Editor editor) {

        // 创建java 引用对象
        JavaReferenceVO javaReferenceVO = new JavaReferenceVO();

        // 获取选择域
        SelectionModel selection = editor.getSelectionModel();

        // 获取选中元素
        PsiElement selectElement = psiJavaFile.findElementAt(selection.getSelectionStart());

        // 获取真实行号, 纵使编辑器折叠也没事
        int lineNum = editor.getDocument().getLineNumber(selection.getSelectionStart()) + 1;
        javaReferenceVO.setLineNo(lineNum);

        // 获取类名
        String className = getClassName(psiJavaFile, editor);
        javaReferenceVO.setClassName(className);

        // 判断选中区域是否是方法
        if (selectElement.getParent() instanceof PsiMethod) { // 如果是方法

            PsiMethod psiMethod = (PsiMethod) selectElement.getParent();

            // 如果为java方法时, 如果不包含括号, 说明方法唯一. 默认方法唯一时, 方法签名不包含形参列表, 自己拼接形参列表
            List<String> methodParams = getMethodParams(psiMethod);

            // 设置参数列表
            javaReferenceVO.setParamList(methodParams);

            // 设置方法名
            javaReferenceVO.setMethodName(psiMethod.getName());

        }else {
            // 设置代码片段
            javaReferenceVO.setCodeLine(selection.getSelectedText());
        }

        return javaReferenceVO;
    }

    /** 获取选中区域所属类权限定名
     * @param psiJavaFile java 文件
     * @param editor 编译器
     * @return 选中区域所属类名
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    private static String getClassName(PsiJavaFile psiJavaFile, Editor editor) {

        // 获取选中区域所属类
        PsiClass psiClass = getPsiClass(psiJavaFile, editor);

        // 判断所属区域是否属于某个类
        if (psiClass != null) { // 如果不为空, 则说明
            return psiClass.getQualifiedName();
        }else{ // 如果选中区域不属于任何一个类, 则将当前文件的名称返回
            return psiJavaFile.getPackageName() + "." + psiJavaFile.getName();
        }
    }

    /** 获取选中区域位于哪个类中. 有内部类或一个文件中有多个类情况
     * @param psiJavaFile java 文件
     * @param editor 编辑器
     * @return 选中区域所属于的java 对象
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    private static PsiClass getPsiClass( PsiJavaFile psiJavaFile, Editor editor) {

        // 获取选中区域开始和结束位置
        int start = editor.getSelectionModel().getSelectionStart();
        int end = editor.getSelectionModel().getSelectionEnd();

        // 如果选中区域开始和结束区域均在类中, 则证明为此类.
        for (PsiClass psiClass : psiJavaFile.getClasses()) {
            // 获取类源码的开始和结束范围
            TextRange textRange = psiClass.getTextRange();
            if(start > textRange.getStartOffset() && start < textRange.getEndOffset()
                    && end > textRange.getStartOffset() && end < textRange.getEndOffset()){
                return psiClass;
            }
        }
        return null;
    }

    /** 获取方法的参数列表
     * @param psiMethod java 方法
     * @return 参数完全限定名称列表
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    private static List<String> getMethodParams(PsiMethod psiMethod) {
        List<String> paramList = new ArrayList<>();
        for (PsiParameter psiParameter : psiMethod.getParameterList().getParameters()) {
            paramList.add(psiParameter.getType().getCanonicalText());
        }
        return paramList;
    }

}
