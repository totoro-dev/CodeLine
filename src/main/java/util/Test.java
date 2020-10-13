package util;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        String path = Test.class.getClassLoader().getResource("mipmap/icon.ico").getPath();
        System.out.println(path.substring(1,path.lastIndexOf("CodeLine")+"CodeLine".length()));
        Calculate.setProject(path.substring(1,path.lastIndexOf("CodeLine")+"CodeLine".length()));
        List<String> list = new ArrayList<>();
        list.add(".java");
        System.out.println(new Calculate().printf(list).getRowCount());
    }
}
