package org.zongf.plugins.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.zongf.plugins.idea.util.CodeTemplateUtil;
import org.zongf.plugins.idea.util.idea.EditorUtil;

import java.util.LinkedHashMap;


/** 基础代码生成工具: 生成setter/getter方法
 * @since 1.0
 * @author zongf
 * @created 2019-07-09
 */
public class BasicCodeAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        // 获取当前类信息
        PsiFile psiFile = event.getData(PlatformDataKeys.PSI_FILE);

        // 如果文件为空
        if (psiFile == null) {
            Messages.showInfoMessage("请打开要生成代码的java文件","未选中合适文件");
            return;
        }

        PsiClass psiClass = PsiTreeUtil.getChildOfType(psiFile.getOriginalElement(), PsiClass.class);

        // 获取当前类中所有字段
        LinkedHashMap<String, String> fields = getFields(psiClass);

        if (fields == null || fields.size() == 0) {
            Messages.showInfoMessage(psiClass.getName() + "类中没有检测到属性","没有属性");
            return;
        }

        // 获取自动生成代码
        String basicCode = CodeTemplateUtil.getBasicCode(psiClass.getName(), fields);

        // 写入当前文件
        EditorUtil.writeString(event.getData(PlatformDataKeys.EDITOR), basicCode);
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
            int idx = canonicalText.lastIndexOf(".");
            String fieldType = canonicalText.substring(idx + 1);
            fieldMap.put(field.getName(), fieldType);
        }

        return fieldMap;
    }



}
