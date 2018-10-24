package net.coding.codingftp.DTO;

import lombok.Data;
import net.coding.codingftp.pojo.UserTitleInfo;

import java.util.List;

@Data
public class PhoneUserJsonDTO {
    private List<UserTitleInfo> data;
}
