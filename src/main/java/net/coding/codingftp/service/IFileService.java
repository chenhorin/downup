package net.coding.codingftp.service;

import net.coding.codingftp.VO.PicVO;
import net.coding.codingftp.common.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileService {

    String upload(MultipartFile file, String path);

    /*
     * 手机端获取图片列表
     * */
    ServerResponse<List<PicVO>> getPicList(String userName);

    ServerResponse<Integer> getPicNum(String userName);

    List<String> getPicListForPC(String userName);
}
