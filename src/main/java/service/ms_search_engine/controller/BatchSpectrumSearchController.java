package service.ms_search_engine.controller;


import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.ms_search_engine.constant.Ms2SpectrumDataSource;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.model.BatchSpectrumSearchModel;
import service.ms_search_engine.service.BatchSpectrumSearchService;

@RestController
@RequestMapping("/api/batchSearch/")
public class BatchSpectrumSearchController {

    // post /api/batchSearch/file/upload   -> upload file s3, check file, save file path to db, return task id
    // post /api/batchSearch/task/submit -> submit task, check parameter, save task to db, sent to task queue
    // get /api/batchSearch/task/{id} -> get task status by id
    // delete /api/batchSearch/task/{id} -> delete task by id, change task status to delete, s3 data delete
    private final BatchSpectrumSearchService batchSpectrumSearchService;

    @Autowired
    public BatchSpectrumSearchController(BatchSpectrumSearchService batchSpectrumSearchService) {
        this.batchSpectrumSearchService = batchSpectrumSearchService;
    }

    @PostMapping("file/upload")
    public ResponseEntity<BatchSpectrumSearchModel> postFileUpload(
            @RequestParam MultipartFile peakListFile,
            @RequestParam MultipartFile ms2File,
            @RequestParam String mail,
            @RequestParam Ms2SpectrumDataSource ms2spectrumDataSource
            ) {
        BatchSpectrumSearchDto batchSpectrumSearchDto = new BatchSpectrumSearchDto();
        batchSpectrumSearchDto.setPeakListFile(peakListFile);
        batchSpectrumSearchDto.setMs2File(ms2File);
        batchSpectrumSearchDto.setMs2spectrumDataSource(ms2spectrumDataSource);
        batchSpectrumSearchDto.setMail(mail);
        BatchSpectrumSearchModel batchSpectrumSearchModel = batchSpectrumSearchService.postFileUpload(batchSpectrumSearchDto);
        return ResponseEntity.status(HttpStatus.OK).body(batchSpectrumSearchModel);
    }

    @PostMapping("task/submit")
    public ResponseEntity<String> postTaskSubmit() {
        return null;
    }

    @GetMapping("task/{id}")
    public ResponseEntity<BatchSpectrumSearchModel> getTaskInfoById(@PathVariable @NotNull String id) {
        return null;
    }

    @DeleteMapping("task/{id}")
    public ResponseEntity<String> deleteTaskById(@PathVariable @NotNull String id) {
        return null;
    }



}
