package ui;

import top.totoro.file.core.TFile;
import util.Calculate;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Main extends JFrame implements ItemListener {

    public static final List<String> SELECTED_TYPE = new ArrayList<>();
    private static ScheduledFuture refreshFuture;
    private static ScheduledExecutorService refreshService = Executors.newScheduledThreadPool(1);

    private JTextField path = new JTextField();
    private JLabel totalFiles = new JLabel("总文件数：0");
    private JLabel totalLines = new JLabel("总行数：0");
    private JViewport viewport = new JViewport();
    private TypeItem java = new TypeItem(new JCheckBox("Java", true), true, ".java");
    private TypeItem c = new TypeItem(new JCheckBox("C/C++"), false, ".c", ".cpp", ".cc", ".h");
    private TypeItem py = new TypeItem(new JCheckBox("Python"), false, ".py");
    private TypeItem php = new TypeItem(new JCheckBox("PHP"), false, ".php");
    private TypeItem js = new TypeItem(new JCheckBox("JS"), false, ".js");
    private TypeItem css = new TypeItem(new JCheckBox("CSS"), false, ".css");
    private TypeItem html = new TypeItem(new JCheckBox("HTML"), false, ".htm", ".html");
    private TypeItem xml = new TypeItem(new JCheckBox("XML"), false, ".xml");
    private TypeItem jsp = new TypeItem(new JCheckBox("JSP"), false, ".jsp");
    private JCheckBox all = new JCheckBox("全选");

    private boolean selectedAll = false; // 是否已经全选
    private boolean autoSelect = false; // 当前选择过程是否在自动选择

    public Main() {
        super("代码行统计");
        setLayout(null);
        setSize(650, 550);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(getClass().getClassLoader().getResource("img/code.png")).getImage());
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);

        SELECTED_TYPE.add(".java");

        new Thread(() -> {
            Font font = new Font(Font.SERIF, Font.PLAIN, 14);

            JPanel panel = new JPanel(null);
            JLabel pathLabel = new JLabel("项目路径");

            pathLabel.setFont(font);
            path.setFont(font);
            totalFiles.setFont(font);
            totalLines.setFont(font);

            pathLabel.setBounds(0, 10, 60, 30);
            path.setBounds(70, 10, 260, 30);
            totalFiles.setBounds(370, 10, 100, 30);
            totalLines.setBounds(500, 10, 150, 30);

            panel.add(pathLabel);
            panel.add(path);
            panel.add(totalFiles);
            panel.add(totalLines);
            panel.setBounds(10, 0, 600, 50);

            JPanel types = new JPanel(new GridLayout(2, 4));
            types.setBorder(BorderFactory.createTitledBorder("请选择文件类型"));
            types.setBounds(10, 50, 615, 80);
            types.add(java.box);
            types.add(c.box);
            types.add(py.box);
            types.add(php.box);
            types.add(jsp.box);
            types.add(js.box);
            types.add(css.box);
            types.add(html.box);
            types.add(xml.box);
            types.add(all);
            add(types);

            all.addItemListener(Main.this);

            JTable table = new JTable();
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            table.setRowHeight(30);
            model.addColumn("文件路径");
            model.addColumn("代码行数");
            table.getColumnModel().getColumn(0).setPreferredWidth(500);
            table.getColumnModel().getColumn(1).setPreferredWidth(100);
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            viewport.add(table);
            viewport.setPreferredSize(new Dimension(600, 500));
            scrollPane.setViewport(viewport);
            scrollPane.setBounds(10, 130, 618, 370);

            add(scrollPane);
            add(panel);
            setVisible(true);
            setResizable(false);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            path.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                        if (refreshFuture != null) {
                            refreshFuture.cancel(true);
                        }
                        refreshFuture = refreshService.schedule(refresh(), 0, TimeUnit.MILLISECONDS);
                    } else if (e.getKeyChar() == KeyEvent.VK_TAB) {
                        // 路径自动补全
                        String root = path.getText().trim().replace("/", "\\");
                        File rootFile = new File(root.substring(0, root.lastIndexOf("\\")));
                        if (rootFile.exists()) {
                            File[] list = rootFile.listFiles();
                            if (list == null) return;
                            int containFilesCount = 0;
                            for (File file : list) {
                                if (file.isDirectory() && file.getPath().replace("/", "\\").contains(root)) {
                                    root = file.getPath().replace("/", "\\");
                                    containFilesCount++;
                                }
                            }
                            if (containFilesCount == 1) {
                                path.setText(root);
                            }
                        }
                    }
                }
            });

            path.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (refreshFuture != null) {
                        refreshFuture.cancel(true);
                    }
                    refreshFuture = refreshService.schedule(refresh(), 0, TimeUnit.MILLISECONDS);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (refreshFuture != null) {
                        refreshFuture.cancel(true);
                    }
                    refreshFuture = refreshService.schedule(refresh(), 0, TimeUnit.MILLISECONDS);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                }
            });
        }).start();

    }

    private void refreshTable() {
        System.out.println(SELECTED_TYPE);
        TFile.getProperty().setFile(new File(path.getText()));
        viewport.add(new Calculate().printf(SELECTED_TYPE));
        totalFiles.setText("总文件数：" + Calculate.totalFiles);
        totalLines.setText("总行数：" + Calculate.totalLines);
    }

    private Runnable refresh() {
        return () -> {
            String tmp = Calculate.setProject(path.getText().trim().replace("/", "\\"));
            if (tmp.equals(path.getText().trim()))
                refreshTable();
        };
    }

    public static void main(String[] args) {
        new Main();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        JCheckBox item = (JCheckBox) e.getItem();
        if ("全选".equals(item.getText())) {
            // 全选-->非全选
            if (selectedAll && !all.isSelected()) {
                selectedAll = false;
                // 已经全选后取消全选时，如果是手动重新点击“全选”的才恢复之前所有的选择状态
                // 已经全选后，点击其它复选框则不恢复，因为这时不是全选状态，单纯是为了取消选择全选复选框
                if (!autoSelect) {
                    autoSelect = true;
                    java.resetStatus();
                    c.resetStatus();
                    py.resetStatus();
                    php.resetStatus();
                    js.resetStatus();
                    jsp.resetStatus();
                    css.resetStatus();
                    html.resetStatus();
                    xml.resetStatus();
                    refreshTable();
                    autoSelect = false;
                }
                return;
            }
            autoSelect = true; // 说明接下来的操作是自动完成，所以不需要记录手动选择的状态
            java.setSelected(true);
            c.setSelected(true);
            py.setSelected(true);
            php.setSelected(true);
            js.setSelected(true);
            jsp.setSelected(true);
            css.setSelected(true);
            html.setSelected(true);
            xml.setSelected(true);
            refreshTable();
            selectedAll = true; // 说明现在的状态是全选
            autoSelect = false; // 自动修改选中状态结束
            return;
        }
        if (selectedAll) {
            autoSelect = true; // 说明取消全选的操作不是手动的
            all.setSelected(false);
            autoSelect = false; // 自动取消全选操作结束
        }
        switch (item.getText()) {
            case "Java":
                java.resoleType();
                break;
            case "C/C++":
                c.resoleType();
                break;
            case "Python":
                py.resoleType();
                break;
            case "PHP":
                php.resoleType();
                break;
            case "JS":
                js.resoleType();
                break;
            case "JSP":
                jsp.resoleType();
                break;
            case "CSS":
                css.resoleType();
                break;
            case "HTML":
                html.resoleType();
                break;
            case "XML":
                xml.resoleType();
                break;
        }

        if (autoSelect) return;
        refreshTable();
    }

    private class TypeItem {
        private JCheckBox box;
        private boolean selected;
        private String[] types;

        private TypeItem(JCheckBox box, boolean selected, String... types) {
            this.box = box;
            this.selected = selected;
            this.types = types;
            box.addItemListener(Main.this);
        }

        private void resoleType() {
            if (box.isSelected()) {
                SELECTED_TYPE.addAll(Arrays.asList(types));
                if (!autoSelect) {
                    selected = true;
                }
            } else {
                SELECTED_TYPE.removeAll(Arrays.asList(types));
                if (!autoSelect) {
                    selected = false;
                }
            }
        }

        private void resetStatus() {
            setSelected(selected);
        }

        private void setSelected(boolean selected) {
            if (box.isSelected() == selected) return;
            box.setSelected(selected);
        }
    }
}
