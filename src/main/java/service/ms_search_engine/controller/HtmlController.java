package service.ms_search_engine.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlController {

    @GetMapping("/")
    public String index(){

        // 如果加上 index.html 會變成 return index.html
        //return index.html 首頁
        return "index";
    }

    @GetMapping("/msSearch")
    public String msSearch(){

        return "msSearch";
    }

    @GetMapping("/batchSearch")
    public String batchSearch(){

        return "batchSearch";
    }

    @GetMapping("/taskView")
    public String taskView(){

        return "taskView";
    }

    @GetMapping("/aboutUs")
    public String aboutUs(){

        return "aboutUs";
    }

    @GetMapping("/logIn")
    public String logIn(){

        return "logIn";
    }

    @GetMapping("/OAuthSuccess")
    public String OAuthSuccess(){

        return "OAuthSuccessPage";
    }
}
