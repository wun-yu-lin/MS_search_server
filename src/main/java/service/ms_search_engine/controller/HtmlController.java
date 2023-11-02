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

    @GetMapping("/ms2Search")
    public String ms2Search(){

        return "ms2Search";
    }
}
