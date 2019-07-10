package org.zongf.plugins.idea.util.idea;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;

/** idea 编辑器工具
 * @author: zongf
 * @created: 2019-07-09
 * @since 1.0
 */
public class EditorUtil {


    public EditorUtil() {
        super();
    }

    /** 写入字符串. idea 写入字符串需要借助于WriteCommandAction
     * @param editor 编辑器
     * @param content 要插入的字符串内容
     * @since 1.0
     * @author zongf
     * @created 2019-07-09
     */
    public static void writeString(Editor editor, String content) {
        writeString(editor, content, true, true);
    }


        /** 写入字符串. idea 写入字符串需要借助于WriteCommandAction
         * @param editor 编辑器
         * @param content 要插入的字符串内容
         * @param fromLineHead 是否从选中行首开始插入
         * @since 1.0
         * @author zongf
         * @created 2019-07-09
         */
    public static void writeString(Editor editor, String content, boolean fromLineHead) {
        writeString(editor, content, true, fromLineHead);
    }


    /** 写入字符串. idea 写入字符串需要借助于WriteCommandAction
     * @param editor 编辑器
     * @param content 要插入的字符串内容
     * @param deleteSelected 是否删除选中内容
     * @param fromLineHead 是否从选中行首开始插入
     * @since 1.0
     * @author zongf
     * @created 2019-07-09
     */
    public static void writeString(Editor editor, String content, boolean deleteSelected, boolean fromLineHead) {

        WriteCommandAction.runWriteCommandAction(editor.getProject(),()->{

            // 如果需要删除选中区域, 则先删除选中区域
            if (deleteSelected) {

                // 获取选中内容开始位置处的offSet和结束位置的offset,
                // 需要注意的是, 如果从下向上选, 则开始位置的offSet > 结束位置的offset
                int startOffSet = editor.getSelectionModel().getSelectionStart();
                int endOffSet = editor.getSelectionModel().getSelectionEnd();

                // 删除光标选中的代码
                if (startOffSet < endOffSet) {
                    editor.getDocument().deleteString(startOffSet, endOffSet);
                } else {
                    editor.getDocument().deleteString(endOffSet, startOffSet);
                }
            }

            // 如果需要从行首开始插入
            if (fromLineHead) {
                // 删除光标选中的文本之后光标所在行: 光标未选中任何文本, 因此开始位置和结束位置一致
                int startLine = editor.getSelectionModel().getSelectionStartPosition().getLine();
                // 获取当前光标所在行, 行首偏移量
                int lineStartOffSet = editor.getDocument().getLineStartOffset(startLine);

                // 写入新的代码
                editor.getDocument().insertString(lineStartOffSet,content);
            }else {
                editor.getDocument().insertString(editor.getSelectionModel().getSelectionStart(), content);
            }
        });
    }
}
