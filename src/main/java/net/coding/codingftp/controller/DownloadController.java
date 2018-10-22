package net.coding.codingftp.controller;

import com.google.common.collect.Maps;
import net.coding.codingftp.DTO.PicCountByUserDTO;
import net.coding.codingftp.VO.PicVO;
import net.coding.codingftp.common.ServerResponse;
import net.coding.codingftp.service.IFileService;
import net.coding.codingftp.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class DownloadController {
    public static String TOMCAT_PATH = null;


    @Autowired
    private IFileService iFileService;

    private static List<PicCountByUserDTO> picCountByUserDTOS = new ArrayList<>();

    @PostMapping("/upload")
//    @RequestParam(value = "upload_file",required = false) 对应的是表单提交的name
    public ServerResponse upload(HttpSession session, @RequestParam(value = "file", required = false) MultipartFile file,
                                 @RequestParam(value = "UserName") String userName,
                                 @RequestParam(value = "Number") Integer number,
                                 @RequestParam(value = "Type", required = false) Integer type,
                                 HttpServletRequest request) {
        String path = request.getSession().getServletContext().getRealPath(userName);
        TOMCAT_PATH = path;
//      判断是否已经存在当前用户，根据结果来进行Number的保存

        for (PicCountByUserDTO userDTO : picCountByUserDTOS) {
            if (StringUtils.equals(userDTO.getName(), userName)) {
                userDTO.setNumber(number);

                String targetFileName = iFileService.upload(file, path);
                String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + userName + "/" + targetFileName;
                Map fileMap = Maps.newHashMap();
                fileMap.put("uri", targetFileName);
                fileMap.put("url", url);
                return ServerResponse.createBySuccess(fileMap);
            }
        }
        PicCountByUserDTO userDTO = new PicCountByUserDTO();
        userDTO.setName(userName);
        userDTO.setNumber(number);
        picCountByUserDTOS.add(userDTO);

        String targetFileName = iFileService.upload(file, path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + userName + "/" + targetFileName;
        Map fileMap = Maps.newHashMap();
        fileMap.put("uri", targetFileName);
        fileMap.put("url", url);
        return ServerResponse.createBySuccess(fileMap);
    }

    //    PC客户端获取照片数量
    @GetMapping("/DownPhone")
    public Integer getPicNum(HttpSession session, @RequestParam(value = "UserName", required = false) String userName) {
        for (PicCountByUserDTO userDTO : picCountByUserDTOS) {
            if (StringUtils.equals(userDTO.getName(), userName)) {
                return userDTO.getNumber();
            }
        }
        return null;
    }

    //    手机获取图片列表
    @GetMapping("/DownForPhone")
    public ServerResponse getPicList(HttpSession session, @RequestParam(value = "UserName", required = false) String userName,
                                     @RequestParam(value = "Number", defaultValue = "0") Integer number) {
        return iFileService.getPicList(userName);
    }

    //    U3D获取图片url,下载
    @GetMapping("/Login")
    public List<String> getPicList(HttpSession session, @RequestParam(value = "UserName", required = false) String userName) {
        return iFileService.getPicListForPC(userName);
    }
}

