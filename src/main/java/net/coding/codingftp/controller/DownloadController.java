package net.coding.codingftp.controller;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.coding.codingftp.DTO.PhoneUserJsonDTO;
import net.coding.codingftp.DTO.PicCountByUserDTO;
import net.coding.codingftp.VO.PhonePicListAndThemeVO;
import net.coding.codingftp.VO.PhoneURLVO;
import net.coding.codingftp.common.ServerResponse;
import net.coding.codingftp.pojo.UserTitleInfo;
import net.coding.codingftp.service.IFileService;
import net.coding.codingftp.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class DownloadController {
    @Autowired
    private IFileService iFileService;

    public static String TOMCAT_PATH = null;

    @Value("${static.json.file}")
    private Resource jsonFile;


    private static List<PicCountByUserDTO> picCountByUserDTOS = new ArrayList<>();

    @PostMapping("/upload")
//    @RequestParam(value = "upload_file",required = false) 对应的是表单提交的name
    public ServerResponse upload(HttpSession session, @RequestParam(value = "file", required = false) MultipartFile file,
                                 @RequestParam(value = "UserName") String userName,
                                 @RequestParam(value = "Number") Integer number,
                                 @RequestParam(value = "Type", required = false) Integer type,
                                 HttpServletRequest request) {
        if (0 == type) {
            return ServerResponse.createBySuccess();
        }
        String path = request.getSession().getServletContext().getRealPath(userName);
        System.out.println(path);
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
        return 0;
    }

    //    手机获取图片列表
    @GetMapping("/DownForPhone")
    public ServerResponse getPicList(HttpSession session, @RequestParam(value = "UserName", required = false) String userName,
                                     @RequestParam(value = "Number", defaultValue = "0") Integer number) {

        PhonePicListAndThemeVO voData = new PhonePicListAndThemeVO();
        List<String> picList = new ArrayList<>();

        if (StringUtils.isBlank(userName)) {
            picList.add("img/admin/1.png");
            picList.add("img/admin/12.png");
            picList.add("img/admin/mid.png");
            picList.add("img/admin/mid-75.png");
            voData.setThemePics(picList);
            voData.setTitle("魔幻照相馆");
            return ServerResponse.createBySuccess(voData);
        }

        PhonePicListAndThemeVO tempData = iFileService.getPicList(userName).getData();
        if (null != tempData) {
            voData = tempData;
        }


        //组装主题和标题
        String json = null;
        Gson gson = new Gson();
        List<UserTitleInfo> data = new ArrayList<>();
//        读取json配置文件,并且转化为java对象
        try {
            json = new String(IOUtils.readFully(jsonFile.getInputStream(), -1, true));
            PhoneUserJsonDTO phoneUserJsonDTO = gson.fromJson(json,
                    new TypeToken<PhoneUserJsonDTO>() {
                    }.getType());
            if (null == phoneUserJsonDTO) {
                return ServerResponse.createByErrorMessage("配置文件没有添加用户");
            }
            data = phoneUserJsonDTO.getData();

        } catch (IOException e) {
            log.info("静态json文件读取错误");
        }
        if (data.size() == 0) {

            return ServerResponse.createByErrorMessage("Json数据转换失败,数据未获取");
        } else {
            for (UserTitleInfo datum : data) {
                if (StringUtils.equals(userName, datum.getUsername())) {
                    picList.add("img/" + userName + "/1.png");
                    picList.add("img/" + userName + "/12.png");
                    picList.add("img/" + userName + "/mid.png");
                    picList.add("img/" + userName + "/mid-75.png");

                    voData.setTitle(datum.getTitle());
                    voData.setThemePics(picList);
                    return ServerResponse.createBySuccess(voData);
                }
            }
        }
        return ServerResponse.createByError();
    }

    //    U3D转到手机端
    @GetMapping("/Login")
    public PhoneURLVO<String> getPicList(HttpSession session, @RequestParam(value = "UserName", required = false) String userName) {
        for (PicCountByUserDTO userDTO : picCountByUserDTOS) {
            if (StringUtils.equals(userDTO.getName(), userName)) {
                PhoneURLVO URL = new PhoneURLVO();
                URL.setData(PropertiesUtil.getProperty("phone.http.prefix") + "?u=" + userName);
                return URL;
            }
        }
        PhoneURLVO URL = new PhoneURLVO();
        URL.setData(PropertiesUtil.getProperty("phone.http.prefix"));
        return URL;
    }

}

