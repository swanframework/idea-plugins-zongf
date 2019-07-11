package org.zongf.plugins.idea.util.idea;

import com.intellij.openapi.application.ex.ClipboardUtil;
import com.intellij.openapi.ide.CopyPasteManager;

import java.awt.datatransfer.StringSelection;

/** 剪切板操作工具
 * @author: zongf
 * @created: 2019-07-11
 * @since 1.0
 */
public class ClipBoardUtil {

    /** 将字符串内容设置到剪切板中
     * @param content 字符串内容
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    public static void setStringContent(String content) {
        CopyPasteManager.getInstance().setContents(new StringSelection(content));
    }

    /** 获取剪切板中的字符串内容
     * @return 字符串
     * @since 1.0
     * @author zongf
     * @created 2019-07-11
     */
    public static String getStringContent() {
        return ClipboardUtil.getTextInClipboard();
    }

}
