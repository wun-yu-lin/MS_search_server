package service.ms_search_engine.vo;

import lombok.Data;

import java.util.Date;

@Data
public class MemberVO {
    Integer id;
    Date lastLogInTime;
    Date createTime;
    String role;
    String email;
    String logInType;
    String name;
    String pictureSrc;
}
