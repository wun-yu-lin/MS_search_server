package service.ms_search_engine.annotation;

import service.ms_search_engine.constant.MSConstant.USER_ROLE;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用於 api method 上，處理使用者身分驗證使用
 * 給予不同的 UserRoles 清單，確認允許的使用者類型
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MSUserAuth {
    /**
     * 允許的 UserRole 清單
     * 允許多個，其中一個符合即可
     */
    USER_ROLE[] userRoles() default {USER_ROLE.OIDC_USER, USER_ROLE.OAUTH2_USER};

    /**
     * 是否檢查使用者所帶的 serverTokenFromMember 是否 match
     * 此功能通常會在 server 設定類型 api 上開啟，確保安全性。
     */
    boolean checkServerToken() default false;
}
