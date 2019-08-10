package org.zongf.plugins.idea.ui;

import com.intellij.openapi.editor.Editor;
import org.zongf.plugins.idea.cache.MvnVersionResultCache;
import org.zongf.plugins.idea.util.CodeTemplateUtil;
import org.zongf.plugins.idea.util.MvnSearchUtil;
import org.zongf.plugins.idea.util.idea.ClipBoardUtil;
import org.zongf.plugins.idea.util.idea.EditorUtil;
import org.zongf.plugins.idea.vo.SearchResult;
import org.zongf.plugins.idea.vo.VersionResult;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
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

    // 表格标题与数据
    private static String[] tableTitles = new String[]{"name", "groupId", "artifactId", "useages"};
    private static String[][] tableData = new String[20][4];

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
        setBounds(100, 100, 1000, 445);

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
                    List<SearchResult> searchResultList = MvnSearchUtil.search(searchEdTxt.getText());
                    initTableData(searchResultList);
                    searchResultTable.setVisible(false);
                    searchResultTable.setVisible(true);
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

        //
        versionList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                int selectedIndex = versionList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    addBtn.setEnabled(true);
                    copyBtn.setEnabled(true);
                }else {
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
        int selectedRow = searchResultTable.getSelectedRow();
        String groupId = tableData[selectedRow][1];
        String artifactId = tableData[selectedRow][2];

        // 获取版本列表中选中的行
        String selectedValue = (String) versionList.getSelectedValue();
        String version = selectedValue.split(":")[0];

        // 生成代码
        String dependence = CodeTemplateUtil.getMvnDependence(groupId, artifactId, version);
        return dependence;
    }


    /** 初始化表格数据
     * @param list
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    private void initTableData(List<SearchResult> list) {
        // 重新初始化表格
        for (int i = 0; i < list.size() && i < tableData.length; i++) {
            SearchResult searchResult = list.get(i);
            String[] row = new String[6];
            row[0] = searchResult.getTitle();
            row[1] = searchResult.getGroupId();
            row[2] = searchResult.getArtifactId();
            row[3] = searchResult.getUseages();
            tableData[i] = row;
        }
    }



    /** 获取版本号
     * @param groupId 组织id
     * @param artifactId 模块儿id
     * @return List<VersionResult>
     * @since 1.0
     * @author zongf
     * @created 2019-08-10
     */
    private List<VersionResult> getVersion(String groupId, String artifactId){

        // 拼接key
        String key = groupId + ":" + artifactId;

        // 尝试从缓存中获取
        List<VersionResult> versionResults = MvnVersionResultCache.getInstance().get(key);

        // 缓存中不存在，则发送网络查询
        if (versionResults == null) {
            versionResults = MvnSearchUtil.queryVersions(groupId, artifactId);
            MvnVersionResultCache.getInstance().set(key, versionResults);
        }

        return versionResults;
    }

    // 初始化页面不自动生成的组件方法
    private void createUIComponents() {

        List<SearchResult> searchResultList = MvnSearchUtil.queryIndex();
        initTableData(searchResultList);


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

                // 设置选中文字
                searchEdTxt.setText(tableData[row][0]);

                // 获取版本号
                List<VersionResult> versionResults = getVersion(tableData[row][1], tableData[row][2]);

                // 创建列表数据
                DefaultListModel<Object> modeList = new DefaultListModel<>();
                for (VersionResult versionResult : versionResults) {
                    String str = versionResult.getVersion() + " : " + versionResult.getPublishDate();
                    modeList.addElement(new JLabel(str).getText());
                }

                // 刷新列表
                versionList.setModel(modeList);
            }
        };

        // 单元格居中
        DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
        cr.setHorizontalAlignment(JLabel.CENTER);
        searchResultTable.getColumn("useages").setCellRenderer(cr);
    }

}
