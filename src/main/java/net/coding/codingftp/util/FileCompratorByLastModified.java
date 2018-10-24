package net.coding.codingftp.util;

import java.io.File;
import java.util.Comparator;

public class FileCompratorByLastModified implements Comparator<File> {
    @Override
    public int compare(File o1, File o2) {
        String o1TrueName = o1.getName().substring(0, o1.getName().indexOf("."));
        String o2TrueName = o2.getName().substring(0, o2.getName().indexOf("."));
//倒序
        long diff = Long.parseLong(o1TrueName) - Long.parseLong(o2TrueName);
        if (diff > 0) {
            return -1;
        } else if (diff == 0) {
            return 0;
        } else {
            return 1;
        }
    }

}

