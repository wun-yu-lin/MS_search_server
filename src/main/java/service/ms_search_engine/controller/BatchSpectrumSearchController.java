package service.ms_search_engine.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.ms_search_engine.constant.Ms2SpectrumDataSource;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.dto.BatchTaskSearchDto;
import service.ms_search_engine.exception.*;
import service.ms_search_engine.model.BatchSpectrumSearchModel;
import service.ms_search_engine.service.BatchSpectrumSearchService;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/batchSearch/")
public class BatchSpectrumSearchController {
    // post /api/batchSearch/file/upload   -> upload file s3, check file, save file path to db, return task id vv
    // post /api/batchSearch/task/submit -> submit task, check parameter, save task to db, sent to task queue
    // get /api/batchSearch/task -> get all task status of user by parameter (page, size, sort)
    // get /api/batchSearch/task/{id} -> get task status by id
    // delete /api/batchSearch/task/{id} -> delete task by id, change task status to delete, s3 data delete
    private final BatchSpectrumSearchService batchSpectrumSearchService;

    @Autowired
    public BatchSpectrumSearchController(BatchSpectrumSearchService batchSpectrumSearchService) {
        this.batchSpectrumSearchService = batchSpectrumSearchService;
    }

    @PostMapping("file/upload")
    public ResponseEntity<BatchSpectrumSearchModel> postFileUpload(
            @RequestParam @NotNull MultipartFile peakListFile,
            @RequestParam @NotNull MultipartFile ms2File,
            @RequestParam @NotNull String mail,
            @RequestParam @NotNull Ms2SpectrumDataSource ms2spectrumDataSource,
            @RequestParam(defaultValue = "0") int authorId
            ) throws S3DataUploadException, QueryParameterException, DatabaseInsertErrorException {

        //setting max file size
        int peakListFileMaxSize = 100 * 1024 * 1024; // max 100MB
        int ms2FileMaxSize = 1000 * 1024 * 1024; // max 1000MB

        //checkFile size
        if (peakListFile.isEmpty() || ms2File.isEmpty()) {
            throw new S3DataUploadException("File is empty");
        }
        if(peakListFile.getSize() > peakListFileMaxSize){
            throw new S3DataUploadException(MessageFormat.format("PeakListFile size is too large, max size is: {0} MB", (peakListFileMaxSize / 1024 / 1024)));
        }
        if(ms2File.getSize() > ms2FileMaxSize){
            throw new S3DataUploadException(MessageFormat.format("MS2 File size is too large, max size is: {0} MB", (ms2FileMaxSize / 1024 / 1024)));
        }

        //check file type
        List<String> peakListFileAllowedFileExtensions = new ArrayList<>(Arrays.asList("csv"));
        if (!peakListFileAllowedFileExtensions.contains(FilenameUtils.getExtension(peakListFile.getOriginalFilename()))){
            throw new S3DataUploadException("peakListFile type is not valid");
        }

        List<String> ms2FileAllowedFileExtensions = new ArrayList<>(Arrays.asList("mgf"));
        if (!ms2FileAllowedFileExtensions.contains(FilenameUtils.getExtension(ms2File.getOriginalFilename()))){
            throw new S3DataUploadException("ms2File type is not valid");
        }

        //prepare dto
        BatchSpectrumSearchDto batchSpectrumSearchDto = new BatchSpectrumSearchDto();
        batchSpectrumSearchDto.setPeakListFile(peakListFile);
        batchSpectrumSearchDto.setMs2File(ms2File);
        batchSpectrumSearchDto.setMs2spectrumDataSource(ms2spectrumDataSource);
        batchSpectrumSearchDto.setMail(mail);
        batchSpectrumSearchDto.setAuthorId(authorId);
        BatchSpectrumSearchModel batchSpectrumSearchModel = batchSpectrumSearchService.postFileUpload(batchSpectrumSearchDto);
        return ResponseEntity.status(HttpStatus.OK).body(batchSpectrumSearchModel);
    }

    @PostMapping("task/submit")
    public ResponseEntity<String> postTaskSubmit(
            @RequestBody BatchSpectrumSearchModel batchSpectrumSearchModel
    ) throws RedisErrorException, QueryParameterException, DatabaseUpdateErrorException, JsonProcessingException {
        BatchSpectrumSearchDto batchSpectrumSearchDto = new BatchSpectrumSearchDto();
        batchSpectrumSearchDto.setPeakListS3FileSrc(batchSpectrumSearchModel.getS3PeakListSrc());
        batchSpectrumSearchDto.setMs2S3FileSrc(batchSpectrumSearchModel.getS3Ms2FileSrc());
        batchSpectrumSearchDto.setMail(batchSpectrumSearchModel.getMail());
        batchSpectrumSearchDto.setMs2spectrumDataSource(Ms2SpectrumDataSource.valueOf(batchSpectrumSearchModel.getMs2spectrumDataSource()));
        batchSpectrumSearchDto.setTaskId(batchSpectrumSearchModel.getId());
        batchSpectrumSearchDto.setAuthorId(batchSpectrumSearchModel.getAuthorId());
        batchSpectrumSearchDto.setMsTolerance(batchSpectrumSearchModel.getMsTolerance());
        batchSpectrumSearchDto.setMsmsTolerance(batchSpectrumSearchModel.getMsmsTolerance());
        batchSpectrumSearchDto.setSimilarityTolerance(batchSpectrumSearchModel.getSimilarityTolerance());
        batchSpectrumSearchDto.setForwardWeight(batchSpectrumSearchModel.getForwardWeight());
        batchSpectrumSearchDto.setReverseWeight(batchSpectrumSearchModel.getReverseWeight());
        batchSpectrumSearchDto.setSimilarityAlgorithm(batchSpectrumSearchModel.getSimilarityAlgorithm());
        batchSpectrumSearchDto.setIonMode(batchSpectrumSearchModel.getIonMode());

        Boolean isSuccessSubmit = batchSpectrumSearchService.postTaskSubmit(batchSpectrumSearchDto);
        if (!isSuccessSubmit) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("submit task failed");
        }
        return ResponseEntity.status(HttpStatus.OK).body("submit task success");
    }

    @GetMapping("task/{id}")
    public ResponseEntity<BatchSpectrumSearchModel> getTaskInfoById(@PathVariable @NotNull String id) {
        return null;
    }


    @GetMapping("task")
    public ResponseEntity<List<BatchSpectrumSearchModel>> getTaskByParameter(
            @RequestParam(defaultValue = "0") Integer authorId,
            @RequestParam(defaultValue = "0") Integer taskInit,
            @RequestParam(defaultValue = "30") Integer taskOffset
    ) throws QueryParameterException, SQLException {

        //preparation dto
        BatchTaskSearchDto batchTaskSearchDto = new BatchTaskSearchDto();
        batchTaskSearchDto.setAuthorId(authorId);
        batchTaskSearchDto.setTaskInit(taskInit);
        batchTaskSearchDto.setTaskOffset(taskOffset);

        List<BatchSpectrumSearchModel> batchSpectrumSearchModelList = batchSpectrumSearchService.getTaskInfoByParameter(batchTaskSearchDto);

        return ResponseEntity.status(HttpStatus.OK).body(batchSpectrumSearchModelList);
    }

    @DeleteMapping("task/{id}")
    public ResponseEntity<String> deleteTaskById(@PathVariable @NotNull int id) throws QueryParameterException, S3DataUploadException, SQLException {

        batchSpectrumSearchService.deleteTaskById(id);
        return ResponseEntity.status(HttpStatus.OK).body("delete success");
    }



}
