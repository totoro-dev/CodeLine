package util;

public class Type {
    String name;
    String[] suffix;

    public Type(String name, String... suffix) {
        this.name = name;
        this.suffix = suffix;
    }

    public String getName() {
        return name;
    }

    public String[] getSuffix() {
        return suffix;
    }
}
