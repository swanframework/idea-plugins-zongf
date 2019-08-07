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
public class CloseScriptTerminalAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        // 获取project, psiFile, editor
        PsiFile psiFile =  anActionEvent.getData(PlatformDataKeys.PSI_FILE);

        // 获取终端
        ToolWindow terminal = ToolWindowManager.getInstance(psiFile.getProject()).getToolWindow("Terminal");

        // 关闭终端
        terminal.show(()->{
            closeTerminals(psiFile, terminal);
        });
    }



    /** 关闭tab页
     * @param terminalToolWindow 终端窗口
     * @return JBTerminalPanel
     * @since 1.0
     * @author zongf
     * @created 2019-08-06
     */
    private void closeTerminals(PsiFile psiFile, ToolWindow terminalToolWindow) {
        // 获取脚本名称
        String scriptName = StringUtil.subStringAfterLast(psiFile.getVirtualFile().getCanonicalPath(), "/");

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

            // 倒序遍历当前tab
            for (int i = jbTabs.getTabCount() -1; i > 0; i--) {
                // 获取当前Tab名称
                String tabName = jbTabs.getTabAt(i).getText();
                if (tabName.equals(scriptName) || tabName.contains(scriptName + "(")) {
                    jbTabs.removeTab(jbTabs.getTabAt(i));
                }
            }
        }
    }

}
