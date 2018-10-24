package net.coding.codingftp.common.config;

import lombok.extern.slf4j.Slf4j;
import net.coding.codingftp.controller.DownloadController;
import net.coding.codingftp.util.FileUtil;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;

@Slf4j
@Component
@Configurable
@EnableScheduling
public class ScheduledTasks {

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void job() {
        log.info("定时器启动");
        String path = DownloadController.TOMCAT_PATH;

        if (path != null) {
//            获取最后一次上传文件夹的路径
            File file = new File(path);
//          获取根目录下所有文件夹
            deleteFileBySetHour(file.getParent(),1);

        }else {
            log.info("未发现上传文件");
        }
    }
    //    自定义间隔几小时删除文件夹下的文件
    public static void deleteFileBySetHour(String path, int hour) {
        ArrayList<String> dirs = FileUtil.getFileDirs(path);
        for (String dir : dirs) {
//            获取各子文件夹下所有的文件
            ArrayList<File> files = FileUtil.getFiles(dir);
            for (File subFile : files) {
                String fileName = subFile.getName();
                String timestamp = fileName.substring(0, fileName.lastIndexOf("."));
                    /*System.out.println(fileName);
                    subFile.delete();*/
                if ((System.currentTimeMillis() - Long.valueOf(timestamp)) >= hour*3600000) {
                    subFile.delete();
                }
            }
//                String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
    }

}
