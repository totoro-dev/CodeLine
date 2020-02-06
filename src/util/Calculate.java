package util;

import top.totoro.file.core.TFile;
import top.totoro.file.core.io.TReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;

public class Calculate {

    public static long totalLines = 0;
    public int readFileLines(File file) {
        TFile.getProperty().setFile(file);
        TReader reader = new TReader(TFile.getProperty());
        String codeLines[] = reader.getStringByFile().split("\n");
        return codeLines.length;
    }

    public JTable printf() {
        totalLines = 0;
        JTable table = new JTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        table.setRowHeight(30);
        model.addColumn("文件路径");
        model.addColumn("代码行数");
        table.getColumnModel().getColumn(0).setPreferredWidth(500);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);

        File[] list = new FileList().getFileList(TFile.getProperty().getFile());
        String root = TFile.getProperty().getFile().getAbsolutePath();
        for (File file :
                list) {
            String path = file.getAbsolutePath();
            if (path.contains("\\build\\")||path.contains("/build/"))continue;
            path = ".." + path.substring(path.indexOf(root) + root.length());
            path.replace("\\", "/");
            int lines = readFileLines(file);
            totalLines+=lines;

            String row[] = new String[2];
            row[0] = path;
            row[1] = lines + "";
            model.addRow(row);
        }
        table.invalidate();
        return table;
    }
}
