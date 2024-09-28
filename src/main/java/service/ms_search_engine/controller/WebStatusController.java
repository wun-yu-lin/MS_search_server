package service.ms_search_engine.controller;


import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.ms_search_engine.service.WebStatusService;
import service.ms_search_engine.vo.WebStatusVo;

@RestController
@RequestMapping("/api/webStatus")
@Scope("request")
public class WebStatusController extends BaseController {

    private final WebStatusService webStatusService;

    public WebStatusController(WebStatusService webStatusService) {
        this.webStatusService = webStatusService;
    }


    @GetMapping("")
    public ResponseEntity<WebStatusVo> getWebStatus() {
       WebStatusVo webStatusVo =  webStatusService.getWebStatus();

        return ResponseEntity.status(HttpStatus.OK).body(webStatusVo);
    }

}
