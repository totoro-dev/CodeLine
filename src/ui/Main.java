package ui;

import top.totoro.file.core.TFile;
import util.Calculate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class Main extends JFrame {
    public Main() {
        super("代码行统计");
        setLayout(null);
        setBounds(300, 100, 650, 550);

        JPanel panel = new JPanel(null);
        JLabel pathLabel = new JLabel("项目路径");
        JTextField path = new JTextField();
        JButton submit = new JButton("统计");
        JLabel totalLabel = new JLabel("总行数：0");
        pathLabel.setBounds(0, 10, 60, 30);
        path.setBounds(70, 10, 260, 30);
        submit.setBounds(350, 10, 80, 30);
        totalLabel.setBounds(500,10,150,30);
        panel.add(pathLabel);
        panel.add(path);
        panel.add(submit);
        panel.add(totalLabel);
        panel.setBounds(10, 0, 600, 50);

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
        JViewport viewport = new JViewport();
        viewport.add(table);
        viewport.setPreferredSize(new Dimension(600, 500));
        scrollPane.setViewport(viewport);
        scrollPane.setBounds(10, 50, 618, 450);

        add(scrollPane);
        add(panel);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        submit.addActionListener((e -> {
            TFile.getProperty().setFile(new File(path.getText()));
            viewport.add(new Calculate().printf());
            totalLabel.setText("总行数："+Calculate.totalLines);
        }));

        setResizable(false);
    }

    public static void main(String[] args) {
        new Main();
    }
}
