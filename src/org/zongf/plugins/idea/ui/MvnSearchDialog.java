package org.zongf.plugins.idea.ui;

import com.intellij.openapi.editor.Editor;
import org.zongf.plugins.idea.cache.MvnVersionResultCache;
import org.zongf.plugins.idea.util.CodeTemplateUtil;
import org.zongf.plugins.idea.util.MvnSearchUtil;
import org.zongf.plugins.idea.util.common.StringFormatUtil;
import org.zongf.plugins.idea.util.idea.ClipBoardUtil;
import org.zongf.plugins.idea.util.idea.EditorUtil;
import org.zongf.plugins.idea.vo.SearchResult;
import org.zongf.plugins.idea.vo.VersionResult;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MvnSearchDialog extends JDialog {

    private static final long serialVersionUID = 7895980406663797937L;

    private Editor editor;

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
    private JLabel descLb;

    // 表格标题与数据
    private static String[] tableTitles = new String[]{"name", "groupId", "artifactId", "useages"};
    private static final int tableRows = 20;
    private static final int tableCols = 4;
    private static String[][] tableData = new String[tableRows][tableCols];

    private static List<SearchResult> searchResultList = null;
    private static List<VersionResult> versionResultList = null;


    // 是否是测试
    private static boolean isMock = false;

    public MvnSearchDialog(Editor editor) {

        // 当传入editor 为空时, 表示是测试环境
        if(editor == null) isMock = true;

        this.editor = editor;

        // 设置默认情况下输入enter执行哪个按钮
        //        getRootPane().setDefaultButton(copyBtn);

        // 设置最底层面板
        setContentPane(contentPane);

        // 设置对话框宽度和高度
        setBounds(new Rectangle(1000, 455));

        // 设置居中, 必须在setBounds 之后调用
        setLocationRelativeTo(null);

        // 设置对话框为模态对话框
        setModal(true);

        // 设置标题
        setTitle("Search Maven Dependence");

        // 初始化按钮不可用
        addBtn.setEnabled(false);
        copyBtn.setEnabled(false);

        // 设置只可单行选中
        versionList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

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
                    // 清空数据
                    clearData();
                    // 查询数据
                    searchResultList = MvnSearchUtil.searchByKey(searchEdTxt.getText());
                    // 刷新表格数据
                    refreshTableData();
                    // 重新绘制表格
                    searchResultTable.repaint();
                }
            }
        });


        // 响应复制按钮事件
        copyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 获取依赖字符串
                String selectDependence = getSelectDependence();

                // 复制到粘贴板
                if (isMock) {
                    System.out.println(selectDependence);
                }else {
                    ClipBoardUtil.setStringContent(selectDependence);
                }

                // 关闭窗口
                dispose();
            }
        });

        // 响应添加按钮事件
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取依赖字符串
                String selectDependence = getSelectDependence();

                // 写入文件
                if (isMock) {
                    System.out.println(selectDependence);
                }else {
                    EditorUtil.writeString(editor, selectDependence, true);
                }
                // 关闭窗口
                dispose();
            }
        });

        // 版本号选择监听
        versionList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                int selectedIndex = versionList.getSelectedIndex();
                if (selectedIndex >= 0) { // 选中
                    // 设置按钮可点击
                    addBtn.setEnabled(true);
                    copyBtn.setEnabled(true);

                    // 获取选中的内容
                    VersionResult versionResult = versionResultList.get(selectedIndex);

                    // 拼接描述信息
                    StringBuffer sb = new StringBuffer();
                    sb.append("Version: ").append(versionResult.getVersion()).append(",   ")
                      .append("Useage: ").append(versionResult.getUsages()).append(",   ")
                      .append("Publish Date: ").append(versionResult.getPublishDate());

                    // 设置描述信息
                    descLb.setText(sb.toString());

                }else { // 未选中
                    addBtn.setEnabled(false);
                    copyBtn.setEnabled(false);
                }
            }
        });

    }

    /** 获取选择的maven依赖引用字符串
     * @return String
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    private String getSelectDependence() {

        // 获取表格中选中的行
        SearchResult searchResult = searchResultList.get(searchResultTable.getSelectedRow());

        // 获取版本列表中选中的行
        VersionResult versionResult = versionResultList.get(versionList.getSelectedIndex());

        // 生成代码
        return CodeTemplateUtil.getMvnDependence(searchResult.getGroupId(), searchResult.getArtifactId(), versionResult.getVersion());
    }


    /** 初始化表格数据
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    private void refreshTableData() {
        // 重新初始化表格
        for (int i = 0; i < searchResultList.size() && i < tableData.length; i++) {
            SearchResult searchResult = searchResultList.get(i);
            String[] row = new String[6];
            row[0] = searchResult.getTitle();
            row[1] = searchResult.getGroupId();
            row[2] = searchResult.getArtifactId();
            row[3] = searchResult.getUseages();
            tableData[i] = row;
        }
    }

    /** 清理表格
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    private void clearData() {
        // 清理表格
        for (int i = 0; i < tableRows; i++) {
            for (int j = 0; j < tableCols; j++) {
                tableData[i][j] = "";
            }
        }

        // 清理版本号
        versionList.setModel(new DefaultListModel());

        // 清理描述信息
        descLb.setText("");

        // 清楚选中行
        searchEdTxt.grabFocus();
    }

    // 初始化页面不自动生成的组件方法
    private void createUIComponents() {

        // 查询数据
        searchResultList = MvnSearchUtil.searchByKey(null);

        // 刷新列表
        refreshTableData();

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

                // 获取选中的数据
                SearchResult searchResult = searchResultList.get(searchResultTable.getSelectedRow());

                // 设置选中文字
                searchEdTxt.setText(searchResult.getTitle());

                // 设置描述信息
                String description = searchResult.getLastDate() + ":  " + StringFormatUtil.hideOverride(searchResult.getDescription(), 90) ;
                descLb.setText(description);

                // 刷新版本列表
                refreshVersionList(searchResult);

            }
        };

        // 单元格居中
        DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
        cr.setHorizontalAlignment(JLabel.CENTER);
        searchResultTable.getColumn("useages").setCellRenderer(cr);
    }

    /** 刷新版本列表
     * @param searchResult
     * @since 1.0
     * @author zongf
     * @created 2019-08-11
     */
    private void refreshVersionList(SearchResult searchResult){
        // 获取版本号
        versionResultList= MvnVersionResultCache.getInstance().get(searchResult);
        // 创建列表数据
        DefaultListModel<Object> modeList = new DefaultListModel<>();
        for (VersionResult versionResult : versionResultList) {
            String str = versionResult.getPublishDate()+ ":" + versionResult.getUsages() + ":" + versionResult.getVersion() ;
            modeList.addElement(new JLabel(str).getText());
        }

        // 刷新列表
        versionList.setModel(modeList);
    }

}
