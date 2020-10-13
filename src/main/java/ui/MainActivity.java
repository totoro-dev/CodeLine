package ui;

import swing.R;
import top.totoro.file.core.TFile;
import top.totoro.swing.widget.base.Size;
import top.totoro.swing.widget.context.Activity;
import top.totoro.swing.widget.context.PopupWindow;
import top.totoro.swing.widget.context.Toast;
import top.totoro.swing.widget.view.EditText;
import top.totoro.swing.widget.view.RecyclerView;
import top.totoro.swing.widget.view.TextView;
import util.Calculate;
import util.ThreadPoolUtil;
import util.TipAdapter;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class MainActivity extends Activity {

    public static List<String> SELECTED_TYPE = new ArrayList<>();

    private static volatile ScheduledFuture<?> refreshFuture;

    private RecyclerView rvFileTypes;
    private RecyclerView rvFileList;
    private final FileTypeAdapter fileTypeAdapter = new FileTypeAdapter(this);
    private final FileListAdapter fileListAdapter = new FileListAdapter();

    private EditText etPath;
    private TextView tvFileCount;
    private TextView tvLineCount;

    private PopupWindow tipWindow = new PopupWindow();

    @Override
    public void onCreate() {
        super.onCreate();
        setTitle("代码行统计");
        setContentView(R.layout.activity_main);
        rvFileTypes = ((RecyclerView) findViewById(R.id.rvFileTypes));
        rvFileList = ((RecyclerView) findViewById(R.id.rvFileList));
        rvFileTypes.setAdapter(fileTypeAdapter);
        rvFileList.setAdapter(fileListAdapter);

        etPath = ((EditText) findViewById(R.id.etPath));
        tvFileCount = ((TextView) findViewById(R.id.tvFileCount));
        tvLineCount = ((TextView) findViewById(R.id.tvLineCount));

        SELECTED_TYPE.add(".java");

        refreshFileInfoTable();

        // 只有这样设置才能让输入框能响应TAB按键，避免被拦截
        getFrame().setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
        getFrame().setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.emptySet());
        etPath.getComponent().addKeyListener(pathKeyListener);
    }

    private final KeyListener pathKeyListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                refreshFileInfoTable();
            } else if (e.getKeyChar() == KeyEvent.VK_TAB) {
                autoCompletePath();
            }
        }
    };

    /**
     * 自动补全路径
     */
    private void autoCompletePath() {
        // 输入框中指定的当前路径
        String root = etPath.getText().trim().replace("/", "\\");
        tipWindow.dismiss();
        if (root.isEmpty() || !root.contains("\\")) {
            return;
        }
        // 当前路径下所属的文件夹
        File rootFile = new File(root.substring(0, root.lastIndexOf("\\")));
        if (!rootFile.exists()) {
            return;
        }
        File[] list = rootFile.listFiles();
        if (list == null) {
            // 当前路径下不存在文件
            return;
        }
        if (root.endsWith("\\")) {
            tipExistFiles(root, list);
            return;
        }
        String rootLowerCase = root.toLowerCase();
        // 匹配当前路径的所有文件夹路径集合
        List<String> containsDir = new ArrayList<>();
        for (File file : list) {
            if (!file.isDirectory()) {
                // 不是文件夹
                continue;
            }
            String dir = file.getPath().replace("/", "\\");
            String dirLowerCase = dir.toLowerCase();
            // 使用小写做判断，增加匹配精度，因为文件夹是不区分大小写的
            if (dirLowerCase.startsWith(rootLowerCase)) {
                containsDir.add(dir);
            }
        }
        if (containsDir.size() == 1) {
            // 只有一个匹配的，直接完整补全
            etPath.setText(containsDir.get(0));
        } else if (!containsDir.isEmpty()) {
            // 超过一个匹配的文件夹，解析匹配的最长路径
            String target = parserTarget(root, containsDir);
            etPath.setText(target);
            tipNextPaths(target.substring(0, root.lastIndexOf("\\")), containsDir);
        } else {
            if (!root.endsWith("\\")) {
                if (new File(root).isDirectory()) {
                    etPath.setText(root + "\\");
                    autoCompletePath();
                }
            }
        }
    }

    /**
     * 递归解析匹配全部文件夹路径的最长目标路径
     *
     * @param target   当前需要判断的路径
     * @param contains 需要全部匹配的文件夹绝对路径集合
     * @return 匹配通过的路径
     */
    private String parserTarget(String target, List<String> contains) {
        boolean allMatched = true;
        String next = "";
        for (String dir : contains) {
            if (!dir.toLowerCase().startsWith(target.toLowerCase())) {
                // 目前target字段所有的路径没有都匹配
                allMatched = false;
                break;
            }
            if (dir.length() > target.length()) {
                if (!next.isEmpty()) continue;
                // 确定下一个继续匹配的字符
                next = dir.substring(target.length(), target.length() + 1);
            } else {
                // 存在一个完全匹配target路径的文件夹，优先补全该路径
                return target;
            }
        }
        if (allMatched) {
            // 全部存在target字段，说明需要判断下一个字符是否都匹配
            target += next;
        } else {
            // 没有全部匹配，需要减掉最后一个字符
            return target.substring(0, target.length() - 1);
        }
        return parserTarget(target, contains);
    }

    private void tipNextPaths(String root, List<String> contains) {
        StringBuilder tips = new StringBuilder("");
        for (String dir : contains) {
            if (dir.length() >= root.length() && dir.toLowerCase().startsWith(root.toLowerCase())) {
                tips.append(dir.replace(root, ""));
                tips.append(",");
            }
        }
        String[] tipArray = tips.toString().split(",");
        if (tipArray.length == 0) {
            tipWindow.dismiss();
        } else {
            tipWindow = new PopupWindow(R.layout.tip_window, 250, 30 * tipArray.length);
            tipWindow.showAsDrop(etPath);
            ((RecyclerView) tipWindow.findViewById(R.id.rvTipList)).setAdapter(new TipAdapter(this, tipArray));
        }
    }

    private void tipExistFiles(String root, File[] files) {
        StringBuilder tips = new StringBuilder("");
        for (File file : files) {
            if (file.isDirectory()) {
                String dir = file.getPath().replace("/", "\\");
                tips.append(dir.replace(root, "")).append(",");
            }
        }
        String[] tipArray = tips.toString().split(",");
        if (tipArray.length == 0) {
            tipWindow.dismiss();
        } else {
            tipWindow = new PopupWindow(R.layout.tip_window, 250, 30 * tipArray.length);
            tipWindow.showAsDrop(etPath);
            ((RecyclerView) tipWindow.findViewById(R.id.rvTipList)).setAdapter(new TipAdapter(this, tipArray));
        }
    }

    public void autoCompleteByTip(String tip) {
        String root = etPath.getText().trim().replace("/", "\\");
        if (!root.endsWith("\\")) {
            root = root.substring(0, root.lastIndexOf("\\"));
        }
        root += tip;
        etPath.setText(root);
        refreshFileInfoTable();
    }

    /**
     * 刷新目录下的代码文件信息表
     */
    public void refreshFileInfoTable() {
        if (refreshFuture != null) {
            refreshFuture.cancel(true);
        }
        refreshFuture = ThreadPoolUtil.execute(() -> {
            System.out.println(SELECTED_TYPE);
            String tmp = Calculate.setProject(etPath.getText().trim().replace("/", "\\"));
            if (tmp.equals(etPath.getText().trim())) {
                TFile.getProperty().setFile(new File(etPath.getText()));
                Calculate.setProject(etPath.getText());
                fileListAdapter.setFileInfos(new Calculate().printf1(SELECTED_TYPE));
                fileListAdapter.notifyDataSetChange();
                tvFileCount.setText("总文件数：" + Calculate.totalFiles);
                tvLineCount.setText("总行数：" + Calculate.totalLines);
            }
        }, 0);
    }

    public static void main(String[] args) {
        newInstance(new Size(650, 550)).startActivity(MainActivity.class);
    }
}
