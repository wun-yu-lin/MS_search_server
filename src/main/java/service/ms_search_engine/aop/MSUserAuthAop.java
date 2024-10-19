package service.ms_search_engine.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import service.ms_search_engine.annotation.MSUserAuth;
import service.ms_search_engine.config.ServerConfig;
import service.ms_search_engine.constant.MSConstant.*;
import service.ms_search_engine.constant.StatusCode;
import service.ms_search_engine.data.BaseAuthRequest;
import service.ms_search_engine.exception.MsApiException;
import service.ms_search_engine.model.MemberModel;
import service.ms_search_engine.service.MemberService;

import java.util.HashSet;
import java.util.Set;

@Aspect
@Component
public class MSUserAuthAop extends BaseAop {

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private MemberService memberService;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Before(value = "execution(public * service.ms_search_engine.controller..*(..))"
            + " && (@annotation(org.springframework.web.bind.annotation.RequestMapping) "
            + "    || @annotation(org.springframework.web.bind.annotation.PostMapping)"
            + "    || @annotation(org.springframework.web.bind.annotation.GetMapping))"
            + " && @annotation(service.ms_search_engine.annotation.MSUserAuth)")
    public void checkUserAuth(JoinPoint joinPoint) {
        MSUserAuth msUserAuth = (MSUserAuth) getAnnotation(joinPoint, MSUserAuth.class);
        USER_ROLE[] roles = msUserAuth.userRoles();
        boolean isCheckServerToken = msUserAuth.checkServerToken();
        BaseAuthRequest request =  getBaseAuthReqBody(joinPoint);
        if (isCheckServerToken) {
            checkServerToken(request);
        }

        //check userRole
        OAuth2AuthenticationToken oauthToken = getOAuth2AuthenticationToken();
        checkUserRole(roles, oauthToken);
    }


    private void checkUserRole(USER_ROLE[] roles, OAuth2AuthenticationToken token) {
        if (token == null) {
            throw new MsApiException(StatusCode.Base.BASE_AUTH_ERROR, "token is null");
        }
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
        MemberModel memberModel = memberService.getMemberByPrincipalName(authorizedClient.getPrincipalName());
        if (memberModel == null) {
            throw new MsApiException(StatusCode.Base.BASE_AUTH_ERROR, "member is not exist");
        }
        String[] memberRoles = memberModel.getRole().split(",");
        Set<String> roleSet = new HashSet<>();
        for (USER_ROLE role : roles) {
            roleSet.add(role.name());
        }
        for (String memberRole : memberRoles) {
            if (roleSet.contains(memberRole)){
                return;
            }
        }
        throw new MsApiException(StatusCode.Base.BASE_AUTH_ERROR, "Invalid user role");
    }

    private OAuth2AuthenticationToken getOAuth2AuthenticationToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken) {
            return (OAuth2AuthenticationToken) authentication;
        }
        return null;
    }



    private void checkServerToken(BaseAuthRequest req) {

        if (req == null || serverConfig.getServerConfigToken() == null) {
            throw new MsApiException(StatusCode.Base.BASE_PARA_ERROR, "server token is null");
        }
        if (!serverConfig.getServerConfigToken().equals(req.getServerTokenFromMember())) {
            throw new MsApiException(StatusCode.Base.BASE_PARA_ERROR, "server token is invalid!");
        }
    }

}
