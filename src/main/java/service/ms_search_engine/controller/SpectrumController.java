package service.ms_search_engine.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.ms_search_engine.dto.SpectrumQueryParaDto;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.SpectrumDataModel;
import service.ms_search_engine.service.SpectrumService;

import java.util.List;

@RestController
@RequestMapping("/api/spectrum")
@Validated
public class SpectrumController {

    //log operation setting
    private final static Logger log = LoggerFactory.getLogger(SpectrumController.class);

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
    public ResponseEntity<SpectrumDataModel> getSpectrumByID(@PathVariable int id) throws JsonProcessingException {
        SpectrumDataModel spectrumDataModel = spectrumService.getSpectrumByID(id);

        //使用 object mapper 轉成 json string
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = objectMapper.writeValueAsString(spectrumDataModel);
        System.out.println("json的值為：" + jsonStr);
        log.info("Get request for spectrum by id: " + id);
        log.info("json的值為：" + jsonStr);

        //使用 objectMapper 將 json string 轉成 SpectrumDataModel 物件, java 使用 \" 來讓 string 內可以使用 "
        jsonStr = "{\"id\":123,\"compoundDataId\":29,\"compoundClassificationId\":20,\"authorId\":0,\"msLevel\":2,\"precursorMz\":511.3,\"exactMass\":528.309,\"collisionEnergy\":\"30 V\",\"mzError\":-0.00133375,\"lastModify\":1696191895000,\"dateCreated\":1696191895000,\"dataSourceArrayList\":[\"['MassBank', 'LC-MS']\"],\"toolType\":\"LC-ESI-QQ\",\"instrument\":\"QuattroPremier XE, Waters\",\"ionMode\":\"positive\",\"ms2Spectrum\":\"105:7.407407 107:15.315315 109:9.409409 111:4.704705 113:100.000000 114:2.002002 117:0.800801 119:6.306306 121:18.018018 123:5.505506 125:3.103103 127:2.002002 129:1.201201 131:2.402402 133:10.610611 135:10.210210 137:3.503504 139:11.411411 143:2.402402 145:24.324324 147:40.440440 149:6.706707 151:2.002002 153:21.521522 155:1.201201 157:5.905906 159:18.818819 161:30.230230 163:7.807808 165:23.123123 166:1.201201 169:3.103103 171:7.407407 173:7.107107 175:5.105105 177:7.107107 179:3.903904 181:3.903904 183:25.125125 185:8.608609 187:9.009009 189:19.619620 191:3.103103 193:1.201201 195:5.505506 197:10.610611 199:7.407407 201:3.103103 203:3.503504 205:1.601602 207:3.903904 209:4.704705 211:6.306306 213:5.505506 215:4.704705 217:1.601602 219:1.201201 221:2.002002 223:4.304304 225:5.105105 227:4.704705 229:2.002002 231:1.201201 233:1.601602 235:2.702703 237:3.103103 239:4.704705 241:3.103103 243:2.002002 245:1.201201 247:2.002002 249:3.503504 251:7.107107 253:6.706707 255:3.503504 257:2.402402 259:2.002002 261:5.505506 263:3.503504 265:4.304304 267:5.105105 269:5.505506 271:3.503504 273:4.304304 275:3.103103 277:7.107107 279:5.905906 281:5.505506 283:5.905906 285:3.103103 287:2.002002 289:3.503504 291:3.503504 293:2.702703 295:5.505506 297:7.807808 299:5.905906 301:3.103103 303:4.704705 305:5.505506 307:2.402402 309:2.402402 311:2.402402 313:4.304304 315:3.503504 317:4.304304 319:4.304304 321:3.103103 323:11.011011 325:2.402402 327:2.402402 329:3.503504 331:3.503504 333:1.601602 335:2.402402 337:2.002002 339:0.800801 341:6.306306 343:4.304304 345:4.704705 347:2.702703 349:3.903904 351:1.201201 353:1.201201 355:2.002002 357:2.702703 359:3.503504 361:2.002002 363:5.105105 365:2.402402 367:2.702703 369:2.702703 371:1.601602 373:2.402402 375:2.402402 378:1.201201 381:2.402402 383:2.702703 385:1.201201 387:3.503504 389:1.201201 391:1.201201 393:0.800801 397:0.800801 398:1.201201 401:3.103103 403:3.103103 405:1.601602 411:2.002002 413:2.002002 415:0.800801 416:1.201201 418:1.601602 421:2.702703 429:5.105105 431:5.105105 432:0.800801 439:4.704705 447:3.903904 449:4.304304 450:0.800801 457:12.112112 459:0.800801 467:1.601602 475:9.809810 476:0.800801 492:0.800801 493:5.105105 511:5.505506\",\"precursorType\":\"[M+H-H2O]+\"}\n";
        SpectrumDataModel spectrumDataModel1 = objectMapper.readValue(jsonStr, SpectrumDataModel.class);
        System.out.println("spectrumDataModel1 的IonMode值為：" + spectrumDataModel1.getIonMode());

        return ResponseEntity.status(HttpStatus.OK.value()).body(spectrumDataModel);
    }

    @PostMapping("")
    public ResponseEntity<String> postSpectrum(@RequestBody SpectrumDataModel spectrumDataModel) throws DatabaseInsertErrorException, QueryParameterException{
        log.info("Post request for spectrum");
        Boolean isPostSuccess = spectrumService.postSpectrum(spectrumDataModel);
        if (!isPostSuccess) {
            throw new DatabaseInsertErrorException("post spectrum failed");
        }
        return ResponseEntity.status(HttpStatus.OK.value()).body("post spectrum success");
    }


}
