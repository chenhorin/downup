package net.coding.codingftp.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class FileUtil {


    public static ArrayList<File> getFiles(String path) {
        ArrayList<File> files = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        Arrays.sort(tempList, new FileCompratorByLastModified());
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i]);
            }
            if (tempList[i].isDirectory()) {
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
            }
            if (tempList[i].isDirectory()) {
                dir.add(tempList[i].toString());
            }
        }
        return dir;
    }
}
