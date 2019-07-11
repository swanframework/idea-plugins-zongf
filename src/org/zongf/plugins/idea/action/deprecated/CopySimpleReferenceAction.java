package org.zongf.plugins.idea.action.deprecated;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.PsiFile;
import org.zongf.plugins.idea.util.common.StringUtil;
import org.zongf.plugins.idea.util.idea.ClipBoardUtil;

/** 复制引用至剪切板, 使用简单类名
 * @author: zongf
 * @created: 2019-07-11
 * @since 1.0
 */
@Deprecated
public class CopySimpleReferenceAction extends CopyReferenceAction {


    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        // 获取PsiFile 和 selection
        PsiFile psiFile = anActionEvent.getData(PlatformDataKeys.PSI_FILE);

        // 如果打开文件, 则返回
        if (anActionEvent.getData(PlatformDataKeys.EDITOR) == null) {
            return;
        }

        SelectionModel selection = anActionEvent.getData(PlatformDataKeys.EDITOR).getSelectionModel();

        // 执行父类方法, 将引用存储到剪切板
        super.actionPerformed(anActionEvent);

        // 从剪切板中获取内容
        String reference = ClipBoardUtil.getStringContent();

        // 简化字符串
        String simpleReference = simpleReference(reference);

        // 回写至剪切板中
        ClipBoardUtil.setStringContent(simpleReference);

    }

    /** 简化引用字符串
     * @param str 引用字符串
     * @return 简化后的字符串
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    private static String simpleReference(String str) {

        // 是否包含方法
        boolean containsMethod = str.contains("(");

        // 解析方法名
        String className = containsMethod ? StringUtil.subStringBeforeFirst(str, "#") : StringUtil.subStringBeforeFirst(str, ":");
        // 解析行号
        String lineNo = StringUtil.subStringAfterLast(str, ":");
        // 解析参数列表
        String params = containsMethod ?  StringUtil.subStringBetween(str, "(", ")") : "";
        // 解析方法名
        String methodName = str.contains("(") ? "#" + StringUtil.subStringBetween(str, "#", "(") : "";

        // 拼接字符串
        StringBuffer sb = new StringBuffer();
        sb.append(getSimpleName(className));
        if(containsMethod) sb.append(methodName).append(getSimpleParams(params));
        sb.append(":");
        sb.append(lineNo);

        return sb.toString();
    }

    /** 解析参数列表, 将类型全限定名称修改为简单名称
     * @param params 参数列表
     * @return 简单名称参数列表
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    private static String getSimpleParams(String params) {
        StringBuffer sb = new StringBuffer();
        sb.append("(");

        String[] array = params.split(",");
        for (String item : array) {
            sb.append(getSimpleName(item)).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(")");
        return sb.toString();
    }

    /** 获取类简单名称
     * @param className 类全限定名称
     * @return 类简单名称
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    private static String getSimpleName(String className) {
        String separator = className.contains( "/") ? "/" : ".";
        return StringUtil.subStringAfterLast(className, separator);
    }

}
