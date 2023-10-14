package service.ms_search_engine.controller;


import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.ms_search_engine.model.SpectrumDataModel;
import service.ms_search_engine.service.SpectrumService;

import java.util.List;

@RestController
@RequestMapping("/api/spectrum")
@Validated
public class SpectrumController {

    @Autowired
    private SpectrumService spectrumService;


    @GetMapping("/")
    public ResponseEntity<List<SpectrumDataModel>> getSpectrumList(@RequestParam @NotNull int spectrumNum){
        List<SpectrumDataModel> spectrumDataModelList = spectrumService.getSpectrumByParameter(spectrumNum);
        return ResponseEntity.status(HttpStatus.OK.value()).body(spectrumDataModelList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpectrumDataModel> getSpectrumByID(@PathVariable int id){
        SpectrumDataModel spectrumDataModel = spectrumService.getSpectrumByID(id);
        return  ResponseEntity.status(HttpStatus.OK.value()).body(spectrumDataModel);
    }

}
