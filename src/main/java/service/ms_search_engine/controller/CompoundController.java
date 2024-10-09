package service.ms_search_engine.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import service.ms_search_engine.dto.CompoundQueryParaDto;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.CompoundClassificationModel;
import service.ms_search_engine.model.CompoundDataModel;
import service.ms_search_engine.service.CompoundService;

import java.util.List;


@RestController
@RequestMapping("/api/compound")
public class CompoundController extends BaseController {

    private final CompoundService compoundService;

    @Autowired
    public CompoundController(CompoundService compoundService) {
        this.compoundService = compoundService;
    }


    @GetMapping("/compoundData/{id}")
    public ResponseEntity<CompoundDataModel> getCompoundDataByID(
            @PathVariable @NotNull int id) throws QueryParameterException {
        return ResponseEntity.status(HttpStatus.OK).body(compoundService.getCompoundDataByID(id));
    }

    @GetMapping("/compoundClassification/{id}")
    public ResponseEntity<CompoundClassificationModel> getCompoundClassificationByID(
            @PathVariable @NotNull int id) throws QueryParameterException {
        return ResponseEntity.status(HttpStatus.OK).body(compoundService.getCompoundClassificationByID(id));
    }

    @GetMapping("/compoundData")
    public ResponseEntity<List<CompoundDataModel>> getCompoundDataByParameter(
            @RequestParam(required = true) @NotNull String inChiKey
    ) throws QueryParameterException {
        CompoundQueryParaDto compoundQueryParaDto = new CompoundQueryParaDto();
        compoundQueryParaDto.setInChiKey(inChiKey);

        List<CompoundDataModel> compoundDataModelList = compoundService.getCompoundDataByParameter(compoundQueryParaDto);
        return ResponseEntity.status(HttpStatus.OK).body(compoundDataModelList);
    }

    @GetMapping("/compoundClassification")
    public ResponseEntity<List<CompoundClassificationModel>> getCompoundClassificationByParameter(
            @RequestParam(required = true) @NotNull String classificationDirectParent
    ) throws QueryParameterException {
        CompoundQueryParaDto compoundQueryParaDto = new CompoundQueryParaDto();
        compoundQueryParaDto.setClassificationDirectParent(classificationDirectParent);

        List<CompoundClassificationModel> compoundClassificationModelList = compoundService.getCompoundClassificationByParameter(compoundQueryParaDto);
        return ResponseEntity.status(HttpStatus.OK).body(compoundClassificationModelList);
    }

    @PostMapping("/compoundData")
    public ResponseEntity<String> postCompoundData(
            @RequestBody CompoundDataModel compoundDataModel
    ) throws QueryParameterException, DatabaseInsertErrorException {

        compoundService.postCompoundData(compoundDataModel);
        return ResponseEntity.status(HttpStatus.OK).body("post compound data success");
    }

    @PostMapping("/compoundClassification")
    public ResponseEntity<String> postCompoundClassification(
            @RequestBody CompoundClassificationModel  compoundClassificationModel
    ) throws QueryParameterException, DatabaseInsertErrorException {
        compoundService.postCompoundClassification(compoundClassificationModel);
        return ResponseEntity.status(HttpStatus.OK).body("post compound classification data success");
    }


}
