package service.ms_search_engine.constant;

public class MSConstant {

    public static enum  USER_ROLE {
        /**
         * 主要管理者
         */
        ADMIN,

        /**
         * OIDC_USER (目前是 from Google login)
         */
        OIDC_USER,

        /**
         * OAUTH2_USER (目前是 from GitHub login)
         */
        OAUTH2_USER
    }
}
