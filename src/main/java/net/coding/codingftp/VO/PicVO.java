package net.coding.codingftp.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PicVO implements Serializable {

    private static final long serialVersionUID = 6757614921489745744L;

    @JsonProperty("Number")
    private Integer number;

    @JsonProperty("LargeImgUrl")
    private String picURL;

}
