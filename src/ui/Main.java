package ui;

import top.totoro.file.core.TFile;
import util.Calculate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

public class Main extends JFrame {
    public Main() {
        super("代码行统计");
        setLayout(null);
        setBounds(300, 100, 650, 550);
        Toolkit took = Toolkit.getDefaultToolkit();
        Image image = took.getImage("src/ui/code.png");
        setIconImage(image);

        Font font = new Font(Font.SERIF, Font.PLAIN, 14);

        JPanel panel = new JPanel(null);
        JLabel pathLabel = new JLabel("项目路径");
        JTextField path = new JTextField();
        JLabel totalFiles = new JLabel("总文件数：0");
        JLabel totalLines = new JLabel("总行数：0");

        pathLabel.setFont(font);
        path.setFont(font);
        totalFiles.setFont(font);
        totalLines.setFont(font);

        pathLabel.setBounds(0, 10, 60, 30);
        path.setBounds(70, 10, 260, 30);
        totalFiles.setBounds(370, 10, 100, 30);
        totalLines.setBounds(500,10,150,30);

        panel.add(pathLabel);
        panel.add(path);
        panel.add(totalFiles);
        panel.add(totalLines);
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
                if (e.getKeyChar() == KeyEvent.VK_ENTER){
                    TFile.getProperty().setFile(new File(path.getText()));
                    viewport.add(new Calculate().printf());
                    totalFiles.setText("总文件数："+Calculate.totalFiles);
                    totalLines.setText("总行数："+Calculate.totalLines);
                }
            }
        });

    }

    public static void main(String[] args) {
        new Main();
    }
}
