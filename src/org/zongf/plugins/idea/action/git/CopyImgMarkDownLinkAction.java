package org.zongf.plugins.idea.action.git;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang3.StringUtils;
import org.zongf.plugins.idea.util.ShellUtil;
import org.zongf.plugins.idea.util.common.StringUtil;
import org.zongf.plugins.idea.util.idea.ClipBoardUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 获取图片markdown 引用地址
 * @author: zongf
 * @date: 2020-06-22
 */
public class CopyImgMarkDownLinkAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        // 获取project, psiFile, editor
        PsiFile psiFile =  anActionEvent.getData(PlatformDataKeys.PSI_FILE);
        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);

        // 如果选中文件为空或目录, 当选中为多个时, 返回空
        if(psiFile== null || psiFile.isDirectory()) return;

        // 如果不是图片则直接返回
        List<String> imageSuffix = Arrays.asList("png", "jpg", "jpeg", "gif");
        String fileSuffix = StringUtils.substringAfterLast(psiFile.getName(), ".").toLowerCase();
        if (!imageSuffix.contains(fileSuffix)) {
            Messages.showErrorDialog("不支持的图片类型, 仅支持[" + imageSuffix.toString() + "]", "图片类型错误");
            return;
        }

        // 获取git remote 地址
        List<String> shells = new ArrayList<>();
        shells.add("cd " + project.getBasePath());
        shells.add("git remote -v");
        List<String> results = ShellUtil.runShell(shells);

        // 如果结果为空, 则返回
        if (results == null || results.isEmpty()) {
            return;
        }

       showImagePath(results, getRelativePath(psiFile));
    }

    /** 获取相对路径 */
    private String getRelativePath(PsiFile psiFile) {
        String projectPath = psiFile.getProject().getBasePath();
        String fileAbsPath = psiFile.getVirtualFile().getPath();
        return StringUtils.substringAfter(fileAbsPath, projectPath);
    }

    /**
     * @param results git 执行结果
     * @param relativePath
     * @author zongf
     * @date 2020-06-22
     */
    private void showImagePath(List<String> results, String relativePath){
        //results: 9origin  https://github.com/zongf0504/blog-images.git (fetch)]
        //目标: https://raw.githubusercontent.com/zongf0504/blog-images/master/images/mybatis/mybatis-001.png
        //markdown: ![image](https://raw.githubusercontent.com/zongf0504/blog-images/master/images/mybatis/mybatis-001.png)

        String[] array = results.get(0).split("\\s+");
        if (array.length != 3) {
            Messages.showErrorDialog(results.toString(), "获取地址失败");
        }else {

            String[] split = StringUtils.substringBetween(array[1], "github.com/", ".git").split("/");
            String gitUser = split[0];
            String projectName = split[1];
            String branchName="master";
            StringBuffer imgUrlSb = new StringBuffer();
            imgUrlSb.append("![image](")
                    .append("https://raw.githubusercontent.com")
                    .append("/").append(gitUser)
                    .append("/").append(projectName)
                    .append("/").append(branchName)
                    .append(relativePath)
                    .append(")");

            // 复制到剪切板
            ClipBoardUtil.setStringContent(imgUrlSb.toString());

//            Messages.showInfoMessage(imgUrlSb.toString(), "图片地址(已复制)");
        }
    }

}
