package net.coding.codingftp.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PhoneThemeVO implements Serializable {

    private static final long serialVersionUID = -1200584792733910681L;

    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("title")
    private String title;

    @JsonProperty("themeURL")
    private List<String> themePics;
}
