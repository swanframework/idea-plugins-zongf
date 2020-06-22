package org.zongf.plugins.idea.action.git;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import org.zongf.plugins.idea.util.ShellUtil;
import org.zongf.plugins.idea.util.idea.ClipBoardUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 获取git地址
 * @author: zongf
 * @date: 2020-06-22
 */
public class CopyGitRemoteUrlAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        // 获取project, psiFile, editor
        PsiFile psiFile =  anActionEvent.getData(PlatformDataKeys.PSI_FILE);

        // 获取git remote 地址
        List<String> shells = new ArrayList<>();
        shells.add("cd " + psiFile.getProject().getBasePath());
        shells.add("git remote -v");
        List<String> results = ShellUtil.runShell(shells);

        // 如果结果为空, 则返回
        if (results == null || results.isEmpty()) {
            return;
        }

        // 复制git地址
        copyRemoteUrl(results);
    }


    /**
     * @param results git 执行结果
     * @author zongf
     * @date 2020-06-22
     */
    private void copyRemoteUrl(List<String> results){
        String[] array = results.get(0).split("\\s+");
        if (array.length != 3) {
            Messages.showErrorDialog(results.toString(), "获取地址失败");
        }else {

            // 复制到剪切板
            ClipBoardUtil.setStringContent(array[1]);

            Messages.showInfoMessage("已复制:" + array[1], "项目git地址");
        }
    }

}
