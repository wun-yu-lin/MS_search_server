package service.ms_search_engine.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.ms_search_engine.constant.Ms2SpectrumDataSource;
import service.ms_search_engine.dao.MemberDao;
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
import java.util.Objects;

@RestController
@RequestMapping("/api/batchSearch/")
@Scope("request")
public class BatchSpectrumSearchController extends BaseController {
    // post /api/batchSearch/file/upload   -> upload file s3, check file, save file path to db, return task id vv
    // post /api/batchSearch/task/submit -> submit task, check parameter, save task to db, sent to task queue
    // get /api/batchSearch/task -> get all task status of user by parameter (page, size, sort)
    // get /api/batchSearch/task/{id} -> get task status by id
    // delete /api/batchSearch/task/{id} -> delete task by id, change task status to delete, s3 data delete
    private final BatchSpectrumSearchService batchSpectrumSearchService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final MemberDao memberDao;


    @Autowired
    public BatchSpectrumSearchController(BatchSpectrumSearchService batchSpectrumSearchService, OAuth2AuthorizedClientService authorizedClientService, MemberDao memberDao) {
        this.batchSpectrumSearchService = batchSpectrumSearchService;
        this.authorizedClientService = authorizedClientService;
        this.memberDao = memberDao;
    }

    @PostMapping("file/upload")
    public ResponseEntity<BatchSpectrumSearchModel> postFileUpload(
            OAuth2AuthenticationToken authentication,
            @RequestParam @NotNull MultipartFile peakListFile,
            @RequestParam @NotNull MultipartFile ms2File,
            @RequestParam @NotNull String mail,
            @RequestParam @NotNull Ms2SpectrumDataSource ms2spectrumDataSource,
            @RequestParam(defaultValue = "0") int authorId,
            @RequestParam String taskDescription
            ) throws S3DataUploadException, QueryParameterException, DatabaseInsertErrorException {
        //check user Author id
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        if (authorId  != memberDao.getMemberByPrincipalName(authorizedClient.getPrincipalName()).getId()) {
            throw new QueryParameterException("authorId is not valid, authorId must be the same as the user id");
        }


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
        batchSpectrumSearchDto.setTaskDescription(taskDescription);
        BatchSpectrumSearchModel batchSpectrumSearchModel = batchSpectrumSearchService.postFileUpload(batchSpectrumSearchDto);
        return ResponseEntity.status(HttpStatus.OK).body(batchSpectrumSearchModel);
    }

    @PostMapping("task/submit")
    public ResponseEntity<String> postTaskSubmit(
            @RequestBody BatchSpectrumSearchModel batchSpectrumSearchModel,
            OAuth2AuthenticationToken authentication
    ) throws RedisErrorException, QueryParameterException, DatabaseUpdateErrorException, JsonProcessingException {
        //check authorId
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        if (batchSpectrumSearchModel.getAuthorId()  != memberDao.getMemberByPrincipalName(authorizedClient.getPrincipalName()).getId()) {
            throw new QueryParameterException("authorId is not valid, authorId must be the same as the user id");
        }

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
        batchSpectrumSearchDto.setMs1Ms2matchMzTolerance(batchSpectrumSearchModel.getMs1Ms2matchMzTolerance());
        batchSpectrumSearchDto.setMs1Ms2matchRtTolerance(batchSpectrumSearchModel.getMs1Ms2matchRtTolerance());
        batchSpectrumSearchDto.setTaskDescription(batchSpectrumSearchModel.getTaskDescription());

        Boolean isSuccessSubmit = batchSpectrumSearchService.postTaskSubmit(batchSpectrumSearchDto);
        if (!isSuccessSubmit) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("submit task failed");
        }
        return ResponseEntity.status(HttpStatus.OK).body("submit task success");
    }

    @GetMapping("task/{id}")
    public ResponseEntity<BatchSpectrumSearchModel> getTaskInfoById(@PathVariable @NotNull int id) throws QueryParameterException, SQLException {
        BatchSpectrumSearchModel batchSpectrumSearchModel = batchSpectrumSearchService.getTaskInfoById(id);

        return ResponseEntity.status(HttpStatus.OK).body(batchSpectrumSearchModel);
    }


    @GetMapping("task")
    public ResponseEntity<List<BatchSpectrumSearchModel>> getTaskByParameter(
            @RequestParam(defaultValue = "0") Integer authorId,
            @RequestParam(defaultValue = "0") Integer taskInit,
            @RequestParam(defaultValue = "30") Integer taskOffset,
            OAuth2AuthenticationToken authentication
    ) throws QueryParameterException, SQLException {
        //check authorId
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        if (!Objects.equals(authorId, memberDao.getMemberByPrincipalName(authorizedClient.getPrincipalName()).getId())) {
            throw new QueryParameterException("authorId is not valid, authorId must be the same as the user id");
        }

        //preparation dto
        BatchTaskSearchDto batchTaskSearchDto = new BatchTaskSearchDto();
        batchTaskSearchDto.setAuthorId(authorId);
        batchTaskSearchDto.setTaskInit(taskInit);
        batchTaskSearchDto.setTaskOffset(taskOffset);

        List<BatchSpectrumSearchModel> batchSpectrumSearchModelList = batchSpectrumSearchService.getTaskInfoByParameter(batchTaskSearchDto);

        return ResponseEntity.status(HttpStatus.OK).body(batchSpectrumSearchModelList);
    }

    @DeleteMapping("task/{id}")
    public ResponseEntity<String> deleteTaskById(@PathVariable @NotNull int id, OAuth2AuthenticationToken authentication) throws QueryParameterException, S3DataUploadException, SQLException {

        //check authorId
        BatchSpectrumSearchModel batchSpectrumSearchModel=  batchSpectrumSearchService.getTaskInfoById(id);
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        if (batchSpectrumSearchModel.getAuthorId() != memberDao.getMemberByPrincipalName(authorizedClient.getPrincipalName()).getId()) {
            throw new QueryParameterException("authorId is not valid, authorId must be the same as the user id");
        }
//        batchSpectrumSearchService.deleteTaskById(id);
        batchSpectrumSearchService.changeTaskStatusToDelete(id);
        return ResponseEntity.status(HttpStatus.OK).body("delete success");
    }



}
