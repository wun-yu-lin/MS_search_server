package service.ms_search_engine.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.ms_search_engine.model.SpectrumDataModel;
import service.ms_search_engine.service.SpectrumService;

import java.awt.print.Book;
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
    @Operation(summary = "Get a spectrum by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the spectrum",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SpectrumDataModel.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Spectrum not found",
                    content = @Content) })
    @GetMapping("/{id}")
    public ResponseEntity<SpectrumDataModel> getSpectrumByID(@PathVariable int id){
        SpectrumDataModel spectrumDataModel = spectrumService.getSpectrumByID(id);
        return  ResponseEntity.status(HttpStatus.OK.value()).body(spectrumDataModel);
    }

}
