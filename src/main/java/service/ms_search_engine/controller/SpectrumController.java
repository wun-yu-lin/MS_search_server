package service.ms_search_engine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sdk.mssearch.javasdk.logger.SdkLoggerFactory;
import service.ms_search_engine.dto.SpectrumQueryParaDto;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.SpectrumDataModel;
import service.ms_search_engine.service.SpectrumService;

import java.util.List;

@RestController
@RequestMapping("/api/spectrum")
@Validated
@Scope("request")
public class SpectrumController extends BaseController {

    //log operation setting
    private final static Logger log = SdkLoggerFactory.getLogger(SpectrumController.class);

    @Autowired
    private SpectrumService spectrumService;

    @GetMapping("/fuzzy")
    public ResponseEntity<List<SpectrumDataModel>> getSpectrumListFuzzySearch(
            @RequestParam(required = true) String keyWord,
            @RequestParam(defaultValue = "0", required = true) int spectrumInit,
            @RequestParam(defaultValue = "10", required = true) int spectrumOffSet
    ) throws QueryParameterException {
        log.info("Get request for spectrum list with fuzzy search");
        SpectrumQueryParaDto spectrumQueryParaDto = new SpectrumQueryParaDto();
        spectrumQueryParaDto.setKeyWord(keyWord);
        spectrumQueryParaDto.setSpectrumInit(spectrumInit);
        spectrumQueryParaDto.setSpectrumOffSet(spectrumOffSet);
        return ResponseEntity.status(HttpStatus.OK.value()).body(spectrumService.getSpectrumByFuzzySearch(spectrumQueryParaDto));
    }


    @Operation(summary = "Get a spectrum by parameter")
    @GetMapping("")
    public ResponseEntity<List<SpectrumDataModel>> getSpectrumList(
            @RequestParam(defaultValue = "0", required = true) int spectrumInit,
            @RequestParam(defaultValue = "10", required = true) int spectrumOffSet,
            @RequestParam(required = false) String compoundName,
            @RequestParam(required = false) String formula,
            @RequestParam(defaultValue = "0", required = true) int authorId,
            @RequestParam(defaultValue = "1", required = true) int msLevel,
            @RequestParam(required = false) Double maxPrecursorMz,
            @RequestParam(required = false) Double minPrecursorMz,
            @RequestParam(required = false) Double maxExactMass,
            @RequestParam(required = false) Double minExactMass,
            @RequestParam(required = false) String precursorType,
            @RequestParam(required = false) String ionMode,
            @RequestParam(required = false) String ms2Spectrum,
            @RequestParam(defaultValue = "0.5") Double forwardWeight,
            @RequestParam(defaultValue = "0.5") Double reverseWeight,
            @RequestParam(defaultValue = "dotPlot") String ms2SimilarityAlgorithm,
            @RequestParam(defaultValue = "20") Double ms2PeakMatchTolerance,
            @RequestParam(defaultValue = "0.5", required = false) Double ms2SpectrumSimilarityTolerance
    ) throws QueryParameterException {
        log.info("Get request for spectrum list");
        SpectrumQueryParaDto spectrumQueryParaDto = new SpectrumQueryParaDto();
        spectrumQueryParaDto.setSpectrumInit(spectrumInit);
        spectrumQueryParaDto.setSpectrumOffSet(spectrumOffSet);
        spectrumQueryParaDto.setAuthorId(authorId);
        spectrumQueryParaDto.setMsLevel(msLevel);
        spectrumQueryParaDto.setMaxExactMass(maxExactMass);
        spectrumQueryParaDto.setMinExactMass(minExactMass);
        spectrumQueryParaDto.setMaxPrecursorMz(maxPrecursorMz);
        spectrumQueryParaDto.setMinPrecursorMz(minPrecursorMz);
        spectrumQueryParaDto.setPrecursorType(precursorType);
        spectrumQueryParaDto.setIonMode(ionMode);
        spectrumQueryParaDto.setMs2Spectrum(ms2Spectrum);
        spectrumQueryParaDto.setCompoundName(compoundName);
        spectrumQueryParaDto.setFormula(formula);
        spectrumQueryParaDto.setForwardWeight(forwardWeight);
        spectrumQueryParaDto.setReverseWeight(reverseWeight);
        spectrumQueryParaDto.setMs2SpectrumSimilarityTolerance(ms2SpectrumSimilarityTolerance);
        spectrumQueryParaDto.setMs2SimilarityAlgorithm(ms2SimilarityAlgorithm);
        spectrumQueryParaDto.setMs2PeakMatchTolerance(ms2PeakMatchTolerance);

        List<SpectrumDataModel> spectrumDataModelList = spectrumService.getSpectrumByParameter(spectrumQueryParaDto);
        return ResponseEntity.status(HttpStatus.OK.value()).body(spectrumDataModelList);
    }

    @Operation(summary = "Get a spectrum by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the spectrum",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SpectrumDataModel.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Spectrum not found",
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<SpectrumDataModel> getSpectrumByID(@PathVariable int id) {
        SpectrumDataModel spectrumDataModel = spectrumService.getSpectrumByID(id);

        return ResponseEntity.status(HttpStatus.OK.value()).body(spectrumDataModel);
    }

    @PostMapping("")
    public ResponseEntity<String> postSpectrum(@RequestBody SpectrumDataModel spectrumDataModel) throws DatabaseInsertErrorException, QueryParameterException{
        log.info("Post request for spectrum");
        spectrumService.postSpectrum(spectrumDataModel);
        return ResponseEntity.status(HttpStatus.OK.value()).body("post spectrum success");
    }


}
