package util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileList {

    public static final Map<String, List<File>> FILES = new ConcurrentHashMap<>();
    public static final String[] TYPES = new String[]{".java", ".c", ".cpp", ".cc", ".h", ".py", ".php", ".jsp", ".js", ".css", ".htm", ".html", ".xml"};

    public synchronized static void initFileList(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files == null) return;
            for (File subFile : files) {
                if (subFile.isDirectory()) {
                    initFileList(subFile);
                } else {
                    String path = subFile.getAbsolutePath();
                    for (String type : TYPES) {
                        if (path.endsWith(type)) {
                            List<File> list = FILES.get(type);
                            if (list == null) list = new LinkedList<>();
                            list.add(subFile);
                            FILES.put(type, list);
                            break;
                        }
                    }
                }
            }
        }
    }

    public synchronized static File[] getFileList(List<String> types) {
        List<File> files = new LinkedList<>();
        for (String type : types) {
            if (FILES.get(type) != null)
                files.addAll(FILES.get(type));
        }
        return files.toArray(new File[0]);
    }
}
