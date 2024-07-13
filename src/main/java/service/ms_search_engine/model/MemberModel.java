package service.ms_search_engine.model;

import lombok.Data;
import java.util.Date;

@Data
public class MemberModel {
    private Integer id;
    private String principalName;
    private Date lastLogInTime;
    private Date createTime;
    private String role;
    private String email;
    private String logInType;
    private String name;

}
