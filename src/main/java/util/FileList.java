package util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FileList {
    public List<File> fileList = new LinkedList<>();

    private String path = "";
    public File[] getFileList(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File subFile :
                    files) {
                if (subFile.isDirectory()){
                    getFileList(subFile);
                }else if ((path=subFile.getAbsolutePath())!=null&&path.endsWith(".java")){
                    fileList.add(subFile);
                }
            }
        }
        //
        return fileList.toArray(new File[fileList.size()]);
    }
}
