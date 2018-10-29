package net.coding.codingftp.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: 81247
 * @Description: ${Description}
 */
@Data
public class PhonePicListAndThemeVO implements Serializable {

    private static final long serialVersionUID = -491881712413731571L;

    @JsonProperty("picList")
    private List<PicVO> picResultVOList;

    @JsonProperty("title")
    private String title;

    @JsonProperty("themeURL")
    private List<String> themePics;
}
