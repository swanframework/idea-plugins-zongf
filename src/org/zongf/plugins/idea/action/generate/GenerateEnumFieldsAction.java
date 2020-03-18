package org.zongf.plugins.idea.action.generate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import org.apache.commons.lang3.StringUtils;
import org.zongf.plugins.idea.util.idea.ClipBoardUtil;
import org.zongf.plugins.idea.util.idea.EditorUtil;
import org.zongf.plugins.idea.util.idea.PsiClassUtil;

/** 优化基础方法: setter/getter/toString/Constructor
 * @author: zongf
 * @created: 2019-07-12
 * @since 1.0
 */
public class GenerateEnumFieldsAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        // 获取project, psiFile, editor
        PsiFile psiFile =  anActionEvent.getData(PlatformDataKeys.PSI_FILE);
        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);

        // 校验文件: 打开的必须是枚举文件 文件
        if (editor == null || psiFile == null || !(psiFile instanceof PsiJavaFile) ) {
            Messages.showErrorDialog("只能操作枚举类", "文件类型错误");
            return;
        }
        PsiClass psiClass = PsiClassUtil.getPsiClass((PsiJavaFile) psiFile, editor);
        if (psiClass == null || !psiClass.isEnum()) {
            Messages.showErrorDialog("只能操作枚举类", "文件类型错误");
            return;
        }

        // 从剪切板获取
        String comment = ClipBoardUtil.getStringContent();

        // 校验剪切板格式
        if (StringUtils.isEmpty(comment) || !comment.contains("-")) {
            Messages.showErrorDialog("标准格式: value1-desc value2-desc ...", "复制内容格式错误");
            return;
        }

        // 生成枚举代码
        generatevalue(editor, comment);

    }

    /** 将注释转换为java 代码
     * @param comment 注释, 使用空格和-为分割符, 如:  value1-desc1 value2-desc2 value3-desc3 ...
     * @return String
     * @author zongf
     * @date 2020-03-10
     */
    public static void generatevalue(Editor editor, String comment) {

        // 记录错误字段
        StringBuffer errorMessage = new StringBuffer();

        String[] array = comment.trim().split("\\s+");

        StringBuffer sb = new StringBuffer("\n");
        for (String item : array) {
            String[] split = item.split("-");

            // 校验格式是否正确
            if (split.length < 2) {
                errorMessage.append(item).append(", ");
                continue;
            }

            String value = split[0];
            String desc = split[1];

            sb.append("\t/** ").append(value).append(":").append(desc).append(" */").append("\n");
            sb.append("\tVALUE_").append(value).append("(").append(value).append(", \"").append(desc).append("\"), ").append("\n");
            sb.append("\n");
        }
        sb.deleteCharAt(sb.length() - 1);

        // 如果错误信息不为0，则标识有错误信息
        if (errorMessage.length() == 0) {
            // 写入当前文件
            EditorUtil.writeString(editor, sb.toString(), false);
        }else {
            String tips = "标准格式: value1-desc value2-desc ...\n"
                    + "异常格式: " + errorMessage.toString();
            Messages.showErrorDialog(tips, "复制内容格式错误");
        }
    }
}
