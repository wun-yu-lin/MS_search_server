package service.ms_search_engine.controller;


import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.ms_search_engine.data.base.DefaultReqData;
import service.ms_search_engine.data.base.DefaultRespData;
import service.ms_search_engine.data.base.RestReqBody;
import service.ms_search_engine.data.base.RestRespBody;

@RestController
@RequestMapping("/api/v2/spectrum")
@Validated
@Scope("request")
public class SpectrumControllerV2 {

    @RequestMapping(value = "/list", method={RequestMethod.POST})
    @ResponseBody
    public RestRespBody<DefaultRespData> test(@Valid @RequestBody RestReqBody<DefaultReqData>req) {
        return RestRespBody.success();
    }

}
