package net.coding.codingftp.service;

import net.coding.codingftp.common.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {

    String upload(MultipartFile file, String path);

    ServerResponse<Integer> getPicNum(String userName);
}
