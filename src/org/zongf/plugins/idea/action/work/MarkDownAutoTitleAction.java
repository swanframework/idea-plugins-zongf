package org.zongf.plugins.idea.action.work;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.apache.commons.lang3.StringUtils;
import org.zongf.plugins.idea.util.idea.EditorUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * @Description:
 * @author: zongf
 * @date: 2019-08-05 14:49
 */
public class MarkDownAutoTitleAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);

        if (editor == null) {
            Messages.showErrorDialog("请打开文件!", "文件选择错误");
        }

        String[] lines = editor.getDocument().getText().split("\n");

        // 处理文件内容
        String articleContent = handleLines(lines);

        // 写入文件
        WriteCommandAction.runWriteCommandAction(editor.getProject(),()->{
            editor.getDocument().setText(articleContent);
        });

    }

    public static String handleLines(String[] lineList) {

        // 标题序号正则表达式
        String titleSeqRegx = "^\\d{1,}(\\.\\d{1,})*$";

        StringBuilder articleSb = new StringBuilder();

        // 标题序号
        Map<Integer, Integer> levelMapSeq = new TreeMap<>();

        boolean isInCodeArea = false;


        StringBuilder lineSb = new StringBuilder();
        for (String line : lineList) {
            //重置
            lineSb.setLength(0);

            // 标识是否在代码块
            if(line.startsWith("```")){
                isInCodeArea = !isInCodeArea;
            }

            if (isInCodeArea || line.startsWith("# ") || !line.startsWith("#")) {
                // 一级标题和非标题行，不做处理
                articleSb.append(line).append("\n");
            }else {
                String[] array = line.split("\\s+");
                String titleFlag = array[0];
                String titleSeq = array[1];
                Integer titleLevel = titleFlag.length();

                // 删除比当前标题的子标题序号
                Iterator<Map.Entry<Integer, Integer>> iterator = levelMapSeq.entrySet().iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getKey() > titleLevel) {
                        iterator.remove();
                    }
                }

                // 如果标题存在，则自增1，否则初始化为1
                Integer preLevel = levelMapSeq.get(titleLevel);
                if (preLevel == null) {
                    levelMapSeq.put(titleLevel, 1);
                }else {
                    levelMapSeq.put(titleLevel, levelMapSeq.get(titleLevel) + 1);
                }

                // 拼接标题标志, 如: ##, ###
                lineSb.append(titleFlag).append(" ");

                // 拼接标题序号, 如: 2.1.3.4
                levelMapSeq.forEach((level, no)->{
                    if (level <= titleLevel) {
                        lineSb.append(no).append(".");
                    }
                });
                lineSb.deleteCharAt(lineSb.length() - 1);

                // 拼接标题内容, 如: 二级标题
                int start = Pattern.matches(titleSeqRegx, titleSeq) ? 2 : 1;
                for (int i = start; i < array.length; i++) {
                    lineSb.append(" ").append(array[i]);
                }
                articleSb.append(lineSb.toString()).append("\n");
            }
        }

        return articleSb.toString();
    }

}
