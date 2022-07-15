package util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileList {

    public static final Map<String, List<File>> FILES = new ConcurrentHashMap<>();
    public static final String[] TYPES = new String[]{".java", ".c", ".cpp", ".cc", ".h", ".py", ".php", ".jsp", ".js", ".css", ".htm", ".html", ".xml"
            ,".cs"/*since v1.0.2 对C#语言的支持*/,".dart"/*since v1.0.2 对Dart语言的支持*/,".vue"/*since v1.0.3 对Vue的支持*/};

    public synchronized static void initFileList(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files == null) return;
            String dirPath = dir.getAbsolutePath().replace("\\", "/");
            // 排除项目中，编译自动生成的文件
            if (dirPath.endsWith("/build") || dirPath.endsWith("/target") || dirPath.endsWith("/.idea") || dirPath.endsWith("/node_modules")) {
                return;
            }
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

    public static String getFileType(File file){
        String suffix = file.getName().substring(file.getName().lastIndexOf("."));
        for (String type : TYPES) {
            if (suffix.equals(type)){
                return type;
            }
        }
        return null;
    }
}
