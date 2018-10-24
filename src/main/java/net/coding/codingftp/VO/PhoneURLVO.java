package net.coding.codingftp.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PhoneURLVO<T> implements Serializable {

    private static final long serialVersionUID = 4749597958028542690L;

    @JsonProperty("Data")
    private T data;
}
