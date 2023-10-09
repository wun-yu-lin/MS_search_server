package service.ms_search_engine.Controllers;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.ms_search_engine.Spectrum;

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/hello") // test/hello url for api
    public String hello(){
        return "testMethod";
    }

    @RequestMapping("/spectrum") // test/spectrum url for api
    public Spectrum spectrum(){
        Spectrum spectrum = new Spectrum();
        spectrum.setAuthorName("wunyu");
        spectrum.setPrecursor_mz(Float.parseFloat("100.01"));
        spectrum.setFormula("C2H6OH");
        return spectrum;
    }

}
