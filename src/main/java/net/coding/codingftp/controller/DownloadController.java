package net.coding.codingftp.controller;

import com.google.common.collect.Maps;
import net.coding.codingftp.common.ServerResponse;
import net.coding.codingftp.service.IFileService;
import net.coding.codingftp.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
public class DownloadController {
    public static String TOMCAT_PATH = null;

    public static Integer PIC_COUNT = 0;

    @Autowired
    private IFileService iFileService;


    @PostMapping("/upload")
//    @RequestParam(value = "upload_file",required = false) 对应的是表单提交的name
    public ServerResponse upload(HttpSession session, @RequestParam(value = "file", required = false) MultipartFile file,
                                 @RequestParam(value = "UserName", required = false) String userName,
                                 @RequestParam(value = "Number", required = false) Integer number,
                                 @RequestParam(value = "Type", required = false) Integer type,
                                 HttpServletRequest request) {
        PIC_COUNT++;
        String path = request.getSession().getServletContext().getRealPath(userName);
        TOMCAT_PATH = path;
        String targetFileName = iFileService.upload(file, path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + userName + "/" +targetFileName;
        Map fileMap = Maps.newHashMap();
        fileMap.put("uri", targetFileName);
        fileMap.put("url", url);
        return ServerResponse.createBySuccess(fileMap);
    }

    @PostMapping("/picnum")
    public ServerResponse<Integer> getPicNum(HttpSession session, @RequestParam(value = "UserName", required = false) String userName) {
        return iFileService.getPicNum(userName);
    }

    @GetMapping("/DownPhone")
    public ServerResponse getPicList(HttpSession session, @RequestParam(value = "UserName", required = false) String userName,
                                              @RequestParam(value = "Number",defaultValue = "0")Integer number) {
        return iFileService.getPicList(userName);
    }

//    U3D获取图片url
    @GetMapping("/Login")
    public ServerResponse getPicList(HttpSession session, @RequestParam(value = "UserName", required = false) String userName) {
        return iFileService.getPicList(userName);
    }
}

