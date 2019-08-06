package org.zongf.plugins.idea.action.run;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;

import javax.swing.*;
import java.util.List;

/**
 * @Description:
 * @author: zongf
 * @date: 2019-08-05 14:49
 */
public class RunScriptAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        // 获取project, psiFile, editor
        PsiFile psiFile =  anActionEvent.getData(PlatformDataKeys.PSI_FILE);

        // 如果选中文件为空或目录, 当选中为多个时, 返回空
        if(psiFile== null || psiFile.isDirectory()) return;

        // 获取终端窗口
        ToolWindow terminal = ToolWindowManager.getInstance(psiFile.getProject()).getToolWindow("Terminal");

        // 显示终端
        terminal.activate(()->{
            // 新增终端Tab页面
            JComponent root = terminal.getComponent();
            JComponent jPanel = (JComponent) root.getComponent(0);
            JComponent jPanel1 = (JComponent) jPanel.getComponent(0);
            JComponent jPanel2 = (JComponent) jPanel1.getComponent(0);

            // 触发新增按钮
            ActionToolbarImpl actionToolbar = (ActionToolbarImpl) ((SimpleToolWindowPanel) jPanel2).getComponent(1);
            List<AnAction> actions = actionToolbar.getActions();
            actions.get(0).actionPerformed(anActionEvent);

            try {
                Thread.sleep(300l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 触发执行脚本
            new ExecuteScriptAction().actionPerformed(anActionEvent);
        });
    }

}
