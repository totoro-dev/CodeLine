package util;

import top.totoro.file.core.TFile;
import top.totoro.file.core.io.TReader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static util.FileList.FILES;


public class Calculate {

    public static long totalFiles = 0;
    public static long totalLines = 0;
    private static String root = "";
    private static final Map<File, Integer> LINES = new ConcurrentHashMap<>();

    public synchronized static String setProject(String path) {
        if (!new File(path).exists()) {
            return root;
        }
        root = path;
        LINES.clear();
        FILES.clear();
        FileList.initFileList(new File(root));
        return root;
    }

    private int readFileLines(File file) {
        if (LINES.get(file) != null) return LINES.get(file);
        TFile.getProperty().setFile(file);
        TReader reader = new TReader(TFile.getProperty());
        String[] codeLines = reader.getStringByFile().split("\n");
        int lines = codeLines.length;
        // 去除代码中的注释行数
        for (String line : codeLines) {
            String trim = line.trim();
            if (trim.length() == 0 || trim.startsWith("//") || trim.startsWith("/*") || trim.startsWith("*") || (trim.startsWith("<!--") && trim.endsWith("-->"))) {
                lines--;
            }
        }
        LINES.put(file, lines);
        return lines;
    }

    public JTable printf(List<String> types) {
        totalLines = 0;
        totalFiles = 0;
        JTable table = new JTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        table.setRowHeight(30);
        model.addColumn("文件路径");
        model.addColumn("代码行数");
        table.getColumnModel().getColumn(0).setPreferredWidth(500);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);

        File[] list = FileList.getFileList(types);
        totalFiles = list.length;
        for (File file : list) {
            String path = file.getAbsolutePath();
            // 排除项目中，ide自动生成的文件
            if (path.contains("\\build\\") || path.contains("/build/") || path.contains("/.idea/") || path.contains("\\.idea\\"))
                continue;
            path = ".." + path.substring(path.indexOf(root) + root.length());
            path = path.replace("\\", "/");
            int lines = readFileLines(file);
            totalLines += lines;
            String[] row = new String[2];
            row[0] = path;
            row[1] = lines + "";
            model.addRow(row);
        }
        table.invalidate();
        return table;
    }
}
