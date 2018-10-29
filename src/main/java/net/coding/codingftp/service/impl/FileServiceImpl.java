package net.coding.codingftp.service.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.coding.codingftp.VO.PhonePicListAndThemeVO;
import net.coding.codingftp.VO.PicVO;
import net.coding.codingftp.common.ServerResponse;
import net.coding.codingftp.controller.DownloadController;
import net.coding.codingftp.service.IFileService;
import net.coding.codingftp.util.FTPUtil;
import net.coding.codingftp.util.FileUtil;
import net.coding.codingftp.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public ServerResponse<Integer> getPicNum(String userName) {
        if (null == DownloadController.TOMCAT_PATH) {
            return ServerResponse.createByErrorMessage("还未上传任何文件");
        }
        File file = new File(DownloadController.TOMCAT_PATH);

//        获取其最后一个用添加的子文件夹下的父文件夹
        ArrayList<String> fileDirs = FileUtil.getFileDirs(file.getParent());

        for (String fileDir : fileDirs) {
            String fileName = fileDir.substring(fileDir.lastIndexOf("/" ) + 1);
            System.out.println(fileName);
            if (StringUtils.equals(fileName, userName)) {
                ArrayList<File> targetDir = FileUtil.getFiles(fileDir);
                return ServerResponse.createBySuccess(targetDir.size());
            }
        }
        return ServerResponse.createByErrorMessage("未发现此用户");
    }




    @Override
//    手机端获取图片
    public ServerResponse<PhonePicListAndThemeVO> getPicList(String userName) {
        if (null == DownloadController.TOMCAT_PATH) {
            return ServerResponse.createByErrorMessage("还未上传任何文件");
        }

        List<PicVO> picResultVOList = new ArrayList();
        ArrayList<File> targetDir = new ArrayList<>();

        File file = new File(DownloadController.TOMCAT_PATH);
        ArrayList<String> fileDirs = FileUtil.getFileDirs(file.getParent());
        for (String fileDir : fileDirs) {
//            注意linux的盘符是相反的
            String fileName = fileDir.substring(fileDir.lastIndexOf(File.separator) + 1);
            if (StringUtils.equals(fileName, userName)) {
                targetDir = FileUtil.getFiles(fileDir);
            }
        }
        if (targetDir.size() != 0) {
//            计数器
            int number = 0;
//            todo 排序倒序
            for (File fileItem : targetDir) {
                PicVO picVO = new PicVO();
                // 返回相对路径
//              picVO.setPicURL(PropertiesUtil.getProperty("ftp.server.http.prefix") + userName + "/" + fileItem.getName());
                picVO.setPicURL(userName + "/" + fileItem.getName());
                picVO.setNumber(++number);
                picResultVOList.add(picVO);
            }
            //组装PicList还需要添加主题和标题
            PhonePicListAndThemeVO listAndThemeVO = new PhonePicListAndThemeVO();
            listAndThemeVO.setPicResultVOList(picResultVOList);
            return ServerResponse.createBySuccess(listAndThemeVO);
        }else {
            return ServerResponse.createByErrorMessage("该用户无图片");
        }
    }

    @Override
    public List<String> getPicListForPC(String userName) {

        List<String> result = new ArrayList();
        ArrayList<File> targetDir = new ArrayList<>();

        File file = new File(DownloadController.TOMCAT_PATH);
        ArrayList<String> fileDirs = FileUtil.getFileDirs(file.getParent());
        for (String fileDir : fileDirs) {
            String fileName = fileDir.substring(fileDir.lastIndexOf(File.separator ) + 1);
            if (StringUtils.equals(fileName, userName)) {
                targetDir = FileUtil.getFiles(fileDir);
            }
        }

        for (File fileItem : targetDir) {
            // 返回相对路径
//                picVO.setPicURL(PropertiesUtil.getProperty("ftp.server.http.prefix") + userName + "/" + fileItem.getName());
            result.add(PropertiesUtil.getProperty("ftp.server.http.prefix") + userName + "/" + fileItem.getName());
        }
        return result;
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
//      单张图片上传到本地
    private String uploadNative (MultipartFile file, File targetFile){
        try {
            /*for (File fileItem : Lists.newArrayList(targetFile)) {
                file.transferTo(fileItem);
            }*/
            file.transferTo(targetFile);

        } catch (IOException e) {
            log.error("上传文件异常", e);
            return null;
        }
        return targetFile.getName();
    }

//      获取文件夹下对应Username文件夹的文件
    private ArrayList<File> getUserPicFileList(String dirName,String userName) {
        ArrayList<String> fileDirs = FileUtil.getFileDirs(dirName);
        ArrayList<File> targetDir = new ArrayList<>();

        for (String fileDir : fileDirs) {
            String fileName = fileDir.substring(fileDir.lastIndexOf("\\") + 1);
//            是否是目标文件夹
            if (StringUtils.equals(fileName, userName)) {
                targetDir = FileUtil.getFiles(fileDir);
            }
        }
        return targetDir;
    }
}