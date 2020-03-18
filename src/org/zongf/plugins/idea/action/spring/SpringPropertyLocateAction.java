package org.zongf.plugins.idea.action.spring;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.search.PsiShortNamesCache;
import org.apache.commons.lang3.StringUtils;
import org.zongf.plugins.idea.util.NameUtil;
import org.zongf.plugins.idea.util.TxtFileUtil;
import org.zongf.plugins.idea.util.idea.PsiFileUtil;

import java.util.List;

/**
 * 定位
 *
 * @author: zongf
 * @created: 2019-07-12
 * @since 1.0
 */
public class SpringPropertyLocateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        // 获取project, psiFile, editor
        PsiFile psiFile = anActionEvent.getData(PlatformDataKeys.PSI_FILE);
        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);

        // 校验文件: 打开的必须是枚举文件 文件
        if (editor == null || psiFile == null || !(psiFile instanceof PsiJavaFile)) {
            Messages.showErrorDialog("必须是java文件", "文件类型错误");
            return;
        }

        // 解析属性类java代码
        PropertyInfo propertyInfo = this.parsePropertyClass(psiFile.getText().split("\n"));

        if (propertyInfo != null) {
            // 找到属性配置文件
            VirtualFile propertyFile = findPropertyFile(project, propertyInfo);

            // 获取选择的属性
            String selectedField = editor.getSelectionModel().getSelectedText();

            int lineNumber = -1;
            if (StringUtils.isNotEmpty(selectedField)) {
                // 读取配置文件内容
                List<String> lines = TxtFileUtil.readFile(propertyFile.getCanonicalPath());

                // 定位行号
                lineNumber = locateLineNumber(lines, propertyInfo.prefix , selectedField);
                // 如果定位不到, 则尝试转换匈牙利命名二次查找
                if (lineNumber == -1) {
                    lineNumber = locateLineNumber(lines, propertyInfo.prefix , NameUtil.hungarian(selectedField));
                }
            }

            // 打开文件
            PsiFileUtil.openFile(project, propertyFile, lineNumber);

            // 如果找不到匹配的行, 提示
            if (selectedField != null && lineNumber == -1) {
                Messages.showErrorDialog("配置文件中未找到" + selectedField + "对应的配置项!", "未找到配置项");
            }
        } else {
            Messages.showErrorDialog("非法的spring属性配置文件", "文件类型错误");
        }


    }

    /** 查找属性文件, 查询不到抛出异常
     * @param project
     * @param propertyInfo
     * @return VirtualFile
     * @author zongf
     * @date 2020-03-18
     */
    private VirtualFile findPropertyFile(Project project, PropertyInfo propertyInfo) {

        // 获取文件信息
        PsiFile[] files = PsiShortNamesCache.getInstance(project).getFilesByName(propertyInfo.sourceFileName);

        VirtualFile targetFile = null;
        if(files.length == 0) {
            Messages.showErrorDialog("找不到属性文件:" + propertyInfo.sourceFilePath, "未找到属性文件");
        } else if (files.length == 1) {
            targetFile = files[0].getVirtualFile();
        } else if (files.length > 1) {
            int idx = Integer.MAX_VALUE;

            for (PsiFile file : files) {
                int newIdx = file.getVirtualFile().getCanonicalPath().indexOf(propertyInfo.sourceFilePath);

                // 匹配成功
                if (newIdx > -1) {
                    if (newIdx < idx) {
                        idx = newIdx;
                        targetFile = file.getVirtualFile();
                    }
                }
            }
        }
        return targetFile;
    }

    /** 定位行号
     * @param lines 属性文件内容
     * @param prefix 前缀
     * @param key key
     * @return int
     * @author zongf
     * @date 2020-03-18
     */
    public int locateLineNumber(List<String> lines, String prefix, String key) {
        String fullKey = key;
        if (StringUtils.isNotEmpty(prefix)) {
            fullKey = prefix + "." + key;
        }

        // 匹配
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (StringUtils.isNotEmpty(line)) {
                String[] array = line.split("=");
                if (fullKey.trim().equals(array[0].trim())) {
                    return i;
                }
            }
        }
        return -1;
    }

    /** 解析属性配置类
     * @param codes 代码
     * @return PropertyInfo
     * @author zongf
     * @date 2020-03-18
     */
    private PropertyInfo parsePropertyClass(String[] codes) {
        PropertyInfo info = new PropertyInfo();
        for (String line : codes) {
            if (StringUtils.startsWith(line, "@ConfigurationProperties")) {
                info.prefix = StringUtils.substringBetween(line, "\"", "\"").trim();
            } else if (StringUtils.startsWith(line, "@PropertySource")) {
                info.sourceFilePath =  StringUtils.substringBetween(line, "\"", "\"");
                if (info.sourceFilePath.contains(":")) {
                    info.sourceFilePath = StringUtils.substringAfter(info.sourceFilePath,":").trim();
                }
                info.sourceFileName = info.sourceFilePath.contains("/") ? StringUtils.substringAfterLast(info.sourceFilePath, "/") : info.sourceFilePath;

            }
        }

        return info.sourceFilePath != null ? info : null;
    }

    private static class PropertyInfo{
        /** 属性文件前缀 */
        public String prefix;

        /** 属性文件路径 */
        public String sourceFilePath;

        /** 属性文件名称 */
        public String sourceFileName;
    }

}
