package net.coding.codingftp.service.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.coding.codingftp.common.ServerResponse;
import net.coding.codingftp.controller.DownloadController;
import net.coding.codingftp.service.IFileService;
import net.coding.codingftp.util.FTPUtil;
import net.coding.codingftp.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Service("iFileService")
@Slf4j
public class FileServiceImpl implements IFileService {

    @Override
    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        //扩展名
        //abc.jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
//        需要考虑的是定时删除的文件获得状况
//        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        String uploadFileName = System.currentTimeMillis() + "." + fileExtensionName;

        log.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);

//        return uploadFTP(file, targetFile);
        return uploadNative(file,targetFile);
    }


    public ServerResponse<Integer> getPicNum(String userName) {
        File file = new File(DownloadController.TOMCAT_PATH);
        if (file == null) {
            return ServerResponse.createByErrorMessage("还未上传任何文件");
        }
//        获取其最后一个用添加的子文件夹下的父文件夹
        ArrayList<String> fileDirs = FileUtil.getFileDirs(file.getParent());

        for (String fileDir : fileDirs) {
            String fileName = fileDir.substring(fileDir.lastIndexOf("\\" ) + 1);
            System.out.println(fileName);
            if (StringUtils.equals(fileName, userName)) {
                ArrayList<File> targetDir = FileUtil.getFiles(fileDir);
                return ServerResponse.createBySuccess(targetDir.size());
            }
        }
        return ServerResponse.createByErrorMessage("未发现此用户");
    }
//    上传到ftp
    private String uploadFTP (MultipartFile file, File targetFile){
            try {
                file.transferTo(targetFile);
                //文件已经上传成功了

                FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                //已经上传到ftp服务器上

                targetFile.delete();
            } catch (IOException e) {
                log.error("上传文件异常", e);
                return null;
            }
            return targetFile.getName();
        }
//      上传到本地
    private String uploadNative (MultipartFile file, File targetFile){
        try {
            file.transferTo(targetFile);
            //文件已经上传成功了

//            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //已经上传到ftp服务器上

//            targetFile.delete();
        } catch (IOException e) {
            log.error("上传文件异常", e);
            return null;
        }
        return targetFile.getName();
    }

}