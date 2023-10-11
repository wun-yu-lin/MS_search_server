package service.ms_search_engine.controllers;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.ms_search_engine.Spectrum;

@RestController
@RequestMapping("/spectrum")
@Validated
public class SpectrumController {
    @Autowired
    Spectrum spectrum;

//    @GetMapping("/")
//    public String RestGet(){
//        //do something
//        //get all spectrum
//        return  "get request";
//    }

    @GetMapping("/")
    public ResponseEntity<Spectrum> get_spectrum(Spectrum spectrum){
        spectrum.setFormula("test");
        spectrum.setPrecursor_mz(Float.parseFloat("100.01"));
        spectrum.setAuthorName("wunyu");
        return new ResponseEntity<Spectrum>(spectrum, HttpStatus.ACCEPTED); //body, statusCode
    }

    @GetMapping("/{id}")
    public String RestGet_byID(
            @PathVariable @NotNull @Min(100) int id
    ){
        //do something
        //get spectrum by ID
        return  "get request by: " + id;
    }

    @PostMapping("/")
    public  String RestPost_spectrum(
            @RequestBody @Valid Spectrum spectrum,
            @RequestHeader(name = "Content-type") String contentType
    ){
        //do something

        return  "Post request";
    }

    @DeleteMapping("/{id}")
    public String RestDeleteSpectrumById(
        @PathVariable int id
    ){
        //do something
        return "Delete spectrum ID= " +id;
    }

    @PutMapping("/{id}")
    public String RestUpdateSpectrumById(
            @PathVariable int id,
            @RequestBody Spectrum spectrum
    ){
        //do something
        return "Update spectrum ID= " +id;
    }



}

