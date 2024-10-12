package service.ms_search_engine.controller;


import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.DatabaseUpdateErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.MemberModel;
import service.ms_search_engine.service.MemberService;
import service.ms_search_engine.vo.MemberVO;
import java.util.Collection;

@RestController
@RequestMapping("/api/member")
@Scope("request")
public class MemberController extends BaseController {

    private final MemberService memberService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    public MemberController(MemberService memberService, OAuth2AuthorizedClientService authorizedClientService) {
        this.memberService = memberService;
        this.authorizedClientService = authorizedClientService;
    }


    @GetMapping("")
    public ResponseEntity<MemberVO> getMember(@AuthenticationPrincipal OAuth2User oAuth2User,
                                              OAuth2AuthenticationToken authentication
    ) throws QueryParameterException {

        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        MemberModel memberModel = memberService.getMemberByPrincipalName(authorizedClient.getPrincipalName());
        if (memberModel == null) {
            throw new UsernameNotFoundException("member not found");
        }

        MemberVO memberVO = new MemberVO();
        memberVO.setId(memberModel.getId());
        memberVO.setLastLogInTime(memberModel.getLastLogInTime());
        memberVO.setCreateTime(memberModel.getCreateTime());
        memberVO.setRole(memberModel.getRole());
        memberVO.setEmail(memberModel.getEmail());
        memberVO.setLogInType(memberModel.getLogInType());
        memberVO.setName(memberModel.getName());
        //get picture src from oauth2 client type
        if (memberModel.getLogInType().equals("google")) {
            memberVO.setPictureSrc(oAuth2User.getAttribute("picture"));
        } else if (memberModel.getLogInType().equals("github")) {
            memberVO.setPictureSrc(oAuth2User.getAttribute("avatar_url"));
        } else {
            throw new QueryParameterException("logInType not found");
        }





        return ResponseEntity.status(HttpStatus.OK).body(memberVO);
    }

    @PostMapping("")
    public ResponseEntity<MemberVO> postMember(@AuthenticationPrincipal OAuth2User oAuth2User,
                                          OAuth2AuthenticationToken authentication) throws QueryParameterException, DatabaseInsertErrorException, DatabaseUpdateErrorException {

        Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        MemberModel memberModel = memberService.getMemberByPrincipalName(authorizedClient.getPrincipalName());
        if (memberModel == null) {
            MemberModel newMemberModel = new MemberModel();
            newMemberModel.setPrincipalName(authorizedClient.getPrincipalName());
            newMemberModel.setLogInType(authentication.getAuthorizedClientRegistrationId());
            newMemberModel.setRole(authorities.toArray()[0].toString());
            newMemberModel.setCreateTime(DateTime.now().toDate());
            newMemberModel.setLastLogInTime(DateTime.now().toDate());

            //get attribute by oauth2 client type
            if (authentication.getAuthorizedClientRegistrationId().equals("google")) {
                newMemberModel.setName(oAuth2User.getAttribute("given_name"));
                newMemberModel.setEmail(oAuth2User.getAttribute("email"));
            } else if (authentication.getAuthorizedClientRegistrationId().equals("github")) {
                newMemberModel.setName(oAuth2User.getAttribute("login"));
            } else {
                throw new QueryParameterException("logInType not found");
            }

            //insert member
            memberService.postMember(newMemberModel);

            //get member
            memberModel = memberService.getMemberByPrincipalName(authorizedClient.getPrincipalName());
        }else {
            memberService.updateMemberLastLogInTimeByPrincipalName(authorizedClient.getPrincipalName());
        }

        MemberVO memberVO = new MemberVO();
        memberVO.setId(memberModel.getId());
        memberVO.setLastLogInTime(memberModel.getLastLogInTime());
        memberVO.setCreateTime(memberModel.getCreateTime());
        memberVO.setRole(memberModel.getRole());
        memberVO.setEmail(memberModel.getEmail());
        memberVO.setLogInType(memberModel.getLogInType());
        memberVO.setName(memberModel.getName());
        //get picture src from oauth2 client type
        if (memberModel.getLogInType().equals("google")) {
            memberVO.setPictureSrc(oAuth2User.getAttribute("picture"));
        } else if (memberModel.getLogInType().equals("github")) {
            memberVO.setPictureSrc(oAuth2User.getAttribute("avatar_url"));
        } else {
            throw new QueryParameterException("logInType not found");
        }





        return ResponseEntity.status(HttpStatus.OK).body(memberVO);
    }

    @GetMapping("/auth")
    public ResponseEntity<String> getMember(@AuthenticationPrincipal OAuth2User oAuth2User
    ) throws QueryParameterException {
        if (oAuth2User == null) {
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
        }

        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }


}
