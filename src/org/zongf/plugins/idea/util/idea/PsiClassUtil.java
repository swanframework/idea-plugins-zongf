package org.zongf.plugins.idea.util.idea;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.zongf.plugins.idea.util.common.ClassUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author: zongf
 * @created: 2019-07-12
 * @since 1.0
 */
public class PsiClassUtil {

    /** 获取选中区域位于哪个类中. 有内部类或一个文件中有多个类情况
     * @param psiJavaFile java 文件
     * @param editor 编辑器
     * @return 选中区域所属于的java 对象
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    public static PsiClass getPsiClass(PsiJavaFile psiJavaFile, Editor editor) {

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
    public static List<String> getMethodParams(PsiMethod psiMethod) {
        List<String> paramList = new ArrayList<>();
        for (PsiParameter psiParameter : psiMethod.getParameterList().getParameters()) {
            paramList.add(psiParameter.getType().getCanonicalText());
        }
        return paramList;
    }

    /** 获取选中区域所属类权限定名. 如果光标不在任何一个区域, 则返回文件名
     * @param psiJavaFile java 文件
     * @param editor 编译器
     * @return 选中区域所属类名
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    public static String getClassName(PsiJavaFile psiJavaFile, Editor editor) {

        // 获取选中区域所属类
        PsiClass psiClass = PsiClassUtil.getPsiClass(psiJavaFile, editor);

        // 判断所属区域是否属于某个类
        if (psiClass != null) { // 如果不为空, 则说明
            return psiClass.getQualifiedName();
        }else{ // 如果选中区域不属于任何一个类, 则将当前文件的名称返回
            return psiJavaFile.getPackageName() + "." + psiJavaFile.getName();
        }
    }

    /** 获取当前类定义的所有字段
     * @param psiClass 当前类类型
     * @return LinkedHashMap 字段名和字段类型组成的key-value键值对
     * @since 1.0
     * @author zongf
     * @created 2019-07-09
     */
    public static LinkedHashMap<String, String> getFields(PsiClass psiClass) {
        LinkedHashMap<String, String> fieldMap = new LinkedHashMap<>();

        PsiField[] allFields = psiClass.getAllFields();
        for (PsiField field : allFields) {
            String canonicalText = field.getType().getCanonicalText();
            String fieldType = ClassUtil.simpleClassName(canonicalText);
            fieldMap.put(field.getName(), fieldType);
        }

        return fieldMap;
    }

}
