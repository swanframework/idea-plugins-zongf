package org.zongf.plugins.idea.action.run;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.terminal.JBTerminalPanel;
import com.intellij.terminal.JBTerminalWidget;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import org.zongf.plugins.idea.util.common.StringUtil;

import javax.swing.*;

/**
 * @Description:
 * @author: zongf
 * @date: 2019-08-05 14:49
 */
public class ExecuteScriptAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        // 获取project, psiFile, editor
        PsiFile psiFile =  anActionEvent.getData(PlatformDataKeys.PSI_FILE);

        // 获取终端
        ToolWindow terminal = ToolWindowManager.getInstance(psiFile.getProject()).getToolWindow("Terminal");

        // 执行命令
        terminal.activate(()->{
            // 获取最新的终端
            JBTerminalPanel terminalPanel = getLastedTerminalPanel(terminal, anActionEvent);

            // 获取待执行的命令
            String cmd = getCmd(psiFile);

            // 执行命令
            terminalPanel.getTerminalOutputStream().sendString(cmd);

        });
    }


    /** 获取脚本执行命令
     * @param psiFile 脚本文件
     * @return String 脚本命令
     * @since 1.0
     * @author zongf
     * @created 2019-08-06
     */
    private String getCmd(PsiFile psiFile) {
        // 获取文件全路径名称
        String filePath = psiFile.getVirtualFile().getCanonicalPath();

        // 获取文件路径和简单名称
        String fileDir = StringUtil.subStringBeforeLast(filePath, "/");
        String fileName = StringUtil.subStringAfterLast(filePath, "/");

        // 先切入脚本所在目录，然后再执行脚本
        StringBuffer cmdSb = new StringBuffer();
        cmdSb.append("cd ").append(fileDir).append("\n");
        cmdSb.append("clear ").append("\n");
        cmdSb.append("./").append(fileName).append("\n");

        return cmdSb.toString();
    }

    /** 获取新建的Terminal Tab页
     * @param terminalToolWindow 终端窗口
     * @param anAction
     * @return JBTerminalPanel
     * @since 1.0
     * @author zongf
     * @created 2019-08-06
     */
    private JBTerminalPanel getLastedTerminalPanel(ToolWindow terminalToolWindow, AnActionEvent anAction) {
        JBTerminalPanel terminalPanel = null;
        JComponent root = terminalToolWindow.getComponent();
        JComponent jPanel = (JComponent) root.getComponent(0);
        JComponent jPanel1 = (JComponent) jPanel.getComponent(0);
        JComponent jPanel2 = (JComponent) jPanel1.getComponent(0);
        JComponent jPanel3 = (JComponent) jPanel2.getComponent(0);
        JComponent jPanel4 = (JComponent) jPanel3.getComponent(0);
        JComponent jPanel5 = (JComponent) jPanel4.getComponent(0);

        // 当Terminal打开多个Tab页时, jPanel5 组件为JBTabs
        if (jPanel5 instanceof JBTabs) {
            JBTabs jbTabs = (JBTabs) jPanel5;
            // 获取选中的Table
            TabInfo tableInfo = ((JBTabs) jPanel5).getTabAt(((JBTabs) jPanel5).getTabCount()-1);
            TabInfo targetInfo = jbTabs.getTargetInfo();
            JBTerminalWidget component = (JBTerminalWidget) tableInfo.getComponent();
            terminalPanel = (JBTerminalPanel) component.getTerminalPanel();
        } else {
            JComponent jPanel6 = (JComponent) jPanel5.getComponent(0);
            terminalPanel = (JBTerminalPanel) (jPanel6.getComponent(0));
        }
        return terminalPanel;
    }
}
