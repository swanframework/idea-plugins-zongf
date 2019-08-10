package org.zongf.plugins.idea.ui;

import org.zongf.plugins.idea.cache.MvnVersionResultCache;
import org.zongf.plugins.idea.util.MvnSearchUtil;
import org.zongf.plugins.idea.vo.SearchResult;
import org.zongf.plugins.idea.vo.VersionResult;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.*;
import java.util.List;

public class MvnSearchDialog extends JDialog {

    private static final long serialVersionUID = 7895980406663797937L;

    private JPanel contentPane;

    // 复制按钮
    private JButton copyBtn;

    // 添加依赖按钮
    private JButton addBtn;

    // 搜索输入框
    private JTextField searchEdTxt;

    // 表格
    private JTable searchResultTable;

    // 版本号列表
    private JList versionList;

    // 表格标题与数据
    private static String[] tableTitles = new String[]{"name", "groupId", "artifactId", "useages"};
    private static String[][] tableData = new String[20][4];

    public MvnSearchDialog() {

        // 设置最底层面板
        setContentPane(contentPane);

        // 设置对话框宽度和高度
        setBounds(100, 100, 1000, 445);

        // 设置对话框为模态对话框
        setModal(true);

        // 设置标题
        setTitle("Search Maven Dependence");

        // 按钮绑定事件
        copyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
            }
        });

        // 设置关闭窗口时执行的方法
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        // 设置当点击ESC按键时结束此窗口运行
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        searchEdTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    List<SearchResult> searchResultList = MvnSearchUtil.multiSearch(searchEdTxt.getText());
                    doSearch(searchResultList);
                    searchResultTable.setVisible(false);
                    searchResultTable.setVisible(true);
                }
            }
        });

        // 设置默认情况下输入enter执行哪个按钮
        //        getRootPane().setDefaultButton(copyBtn);

    }




    private void doSearch( List<SearchResult> list) {
        // 重新初始化表格
        for (int i = 0; i < list.size(); i++) {
            SearchResult searchResult = list.get(i);
            String[] row = new String[6];
            row[0] = searchResult.getTitle();
            row[1] = searchResult.getGroupId();
            row[2] = searchResult.getArtifactId();
            row[3] = searchResult.getUseages();
            tableData[i] = row;

            if (i == 19) {
                break;
            }
        }

    }



    private List<VersionResult> getVersion(String groupId, String artifactId){
        String key = groupId + ":" + artifactId;

        List<VersionResult> versionResults = MvnVersionResultCache.getInstance().get(key);

        if (versionResults == null) {
            versionResults = MvnSearchUtil.queryVersions(groupId, artifactId);
            MvnVersionResultCache.getInstance().set(key, versionResults);
        }

        return versionResults;
    }

    // 初始化页面不自动生成的组件方法
    private void createUIComponents() {

        List<SearchResult> searchResultList = MvnSearchUtil.queryIndex();
        doSearch(searchResultList);


        // 创建表格
        searchResultTable = new JTable(tableData, tableTitles){

            private static final long serialVersionUID = 8318732937243012756L;

            // 设置表格不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            // 设置只能单选
            @Override
            public void setSelectionMode(int selectionMode) {
                super.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            }

            @Override
            public void valueChanged(ListSelectionEvent e) {
                super.valueChanged(e);

                // 获取选中行数
                int row = searchResultTable.getSelectedRow();

                searchEdTxt.setText(tableData[row][0]);

                // 获取版本号
                List<VersionResult> versionResults = getVersion(tableData[row][1], tableData[row][2]);

                // 创建列表数据
                DefaultListModel<Object> modeList = new DefaultListModel<>();
                for (VersionResult versionResult : versionResults) {
                    String str = "version: " + versionResult.getVersion() + ", date: " + versionResult.getPublishDate();
                    modeList.addElement(new JLabel(str).getText());
                }

                // 刷新列表
                versionList.setModel(modeList);
            }
        };



        DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
        cr.setHorizontalAlignment(JLabel.CENTER);
        searchResultTable.getColumn("useages").setCellRenderer(cr);

        // 初始化时不显示表格
//        searchResultTable.setVisible(false);

    }


    public static void main(String[] args) {
        MvnSearchDialog dialog = new MvnSearchDialog();
        // 自动调整初始化大小
//        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
