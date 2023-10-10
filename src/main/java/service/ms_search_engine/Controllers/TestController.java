package service.ms_search_engine.Controllers;
import org.springframework.web.bind.annotation.*;
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

    @RequestMapping(value = "/getMethod", method = RequestMethod.GET)
    @GetMapping("/getMethods")
    public String get_parameter(@RequestParam(required = false) String message, // required = false 代表不一定要賦予參數數值
                                @RequestParam(defaultValue = "test_user") String name){
        System.out.println("message: " + message);
        System.out.println("author: " + name);
        return "message: " + message;

    }
    @RequestMapping("/getMethod/{id}/{name}")
    public String get_parameter_by_id(@PathVariable int id,
                                      @PathVariable String name ){
        System.out.println("Id: " + id);
        System.out.println("Name: " + name);
        return "Id: " + id;

    }

    @RequestMapping("postMethod")
    public String get_parameter(@RequestBody Spectrum spectrum,
                                @RequestHeader String headerName,
                                @RequestHeader(name="Content-type") String contentType
                                ){
        System.out.println("Header name: "+ headerName);
        System.out.println("content-type: "+ contentType);
        System.out.println(spectrum.getAuthorName());
        return "postMethods";
    }


    @GetMapping("/exception")
    public String exceptionTest(){
        throw new RuntimeException("Test RunTimeException invoke");
    }




}
