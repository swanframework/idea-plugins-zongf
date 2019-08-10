package org.zongf.plugins.idea.util.dialog;

import org.zongf.plugins.idea.ui.MvnSearchDialog;

/**
 * @Description:
 * @author: zongf
 * @date: 2019-08-10 19:26
 */
public class MvnSearchDialogTest {

    public static void main(String[] args) {
        MvnSearchDialog dialog = new MvnSearchDialog(null);
        // 自动调整初始化大小
//        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

}
