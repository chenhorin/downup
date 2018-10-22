package net.coding.codingftp.util;

import java.io.File;
import java.util.ArrayList;

public class FileUtil {



    public static ArrayList<File> getFiles(String path) {
        ArrayList<File> files = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
//              System.out.println("文     件：" + tempList[i]);
                files.add(tempList[i]);
            }
            if (tempList[i].isDirectory()) {
//              System.out.println("文件夹：" + tempList[i]);
            }
        }
        return files;
    }

    public static ArrayList<String> getFileDirs(String path) {
        ArrayList<String> dir = new ArrayList<String>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                System.out.println("文     件：" + tempList[i]);
            }
            if (tempList[i].isDirectory()) {
//                System.out.println("文件夹：" + tempList[i]);
                dir.add(tempList[i].toString());
            }
        }
        return dir;
    }
}
