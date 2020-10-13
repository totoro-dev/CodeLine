package util;

public class FileInfo {
    private String path;
    private int line;

    public FileInfo(String path, int line) {
        this.path = path;
        this.line = line;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
