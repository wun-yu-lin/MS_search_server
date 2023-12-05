package service.ms_search_engine.controller;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {


    @GetMapping("")
    public Map<String, Object> getUser(@AuthenticationPrincipal OAuth2User oAuth2User) throws JsonGenerationException, JsonMappingException, IOException {

        Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();
        System.out.println("authorities = " + authorities);


        return oAuth2User.getAttributes();
    }

}
