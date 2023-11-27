package service.ms_search_engine.controller;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {


    @GetMapping("")
    public Map<String, Object> getUser(@AuthenticationPrincipal OAuth2User oAuth2User) throws JsonGenerationException, JsonMappingException, IOException {
        return oAuth2User.getAttributes();
    }

}
