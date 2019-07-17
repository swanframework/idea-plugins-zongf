package org.zongf.plugins.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.zongf.plugins.idea.util.JavaReferenceUtil;
import org.zongf.plugins.idea.util.common.ClassUtil;
import org.zongf.plugins.idea.util.idea.ClipBoardUtil;
import org.zongf.plugins.idea.vo.JavaReferenceVO;

/** 复制引用至剪切板, 使用全限定类名
 * @author: zongf
 * @created: 2019-07-11
 * @since 1.0
 */
public class JavaCopyReferenceSimpleAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        // 获取PsiFile 和 selection
        PsiFile psiFile = anActionEvent.getData(PlatformDataKeys.PSI_FILE);
        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);

        // 校验文件: 打开的必须是java 文件
        if (editor == null || psiFile == null || !(psiFile instanceof PsiJavaFile)) {
            Messages.showErrorDialog("请打开java文件!", "文件类型错误");
            return;
        }

        // 解析当前文件文件
        JavaReferenceVO javaReferenceVO = JavaReferenceUtil.parser((PsiJavaFile) psiFile, editor);

        // 处理类名
        javaReferenceVO.setClassName(ClassUtil.simpleClassName(javaReferenceVO.getClassName()));

        // 将java引用对象转换为字符串
        String reference = JavaReferenceUtil.buildReference(javaReferenceVO, className -> ClassUtil.simpleClassName(className));

        // 将内容设置到粘贴板中
        ClipBoardUtil.setStringContent(reference);
    }
}
