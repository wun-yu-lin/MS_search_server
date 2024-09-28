package service.ms_search_engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import service.ms_search_engine.constant.TaskStatus;
import service.ms_search_engine.dao.BatchSearchRdbDao;
import service.ms_search_engine.dao.BatchSearchS3FileDao;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.dto.BatchTaskSearchDto;
import service.ms_search_engine.exception.*;
import service.ms_search_engine.model.BatchSpectrumSearchModel;
import service.ms_search_engine.redisService.RedisMailQueueService;
import service.ms_search_engine.redisService.RedisSentTaskMailVO;
import service.ms_search_engine.redisService.RedisTaskQueueService;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Component
public class BatchSpectrumSearchServiceImpl implements BatchSpectrumSearchService {

    @Value("${aws.cloudFront.endpoint}")
    private String awsCloudFrontEndpoint;

    private final BatchSearchRdbDao batchSearchRdbDao;
    private final BatchSearchS3FileDao batchSearchS3FileDao;
    private final RedisTaskQueueService redisTaskQueueService;

    private final RedisMailQueueService redisMailQueueService;

    @Autowired
    public BatchSpectrumSearchServiceImpl(BatchSearchRdbDao batchSearchRdbDao,
                                          BatchSearchS3FileDao batchSearchS3FileDao,
                                          RedisTaskQueueService redisTaskQueueService, RedisMailQueueService redisMailQueueService) {
        this.batchSearchRdbDao = batchSearchRdbDao;
        this.batchSearchS3FileDao = batchSearchS3FileDao;
        this.redisTaskQueueService = redisTaskQueueService;
        this.redisMailQueueService = redisMailQueueService;
    }

    @Override
    public BatchSpectrumSearchModel postFileUpload(BatchSpectrumSearchDto batchSpectrumSearchDto) throws S3DataUploadException, QueryParameterException, DatabaseInsertErrorException {
        BatchSpectrumSearchDto dtoAfterUploadS3 = batchSearchS3FileDao.postFileUpload(batchSpectrumSearchDto);
        if (dtoAfterUploadS3.getMs2S3FileSrc() == null || dtoAfterUploadS3.getPeakListFile() == null) {
            //if upload failed, delete the record in s3 and return error
            batchSearchS3FileDao.deleteFileByKey(dtoAfterUploadS3.getMs2S3FileSrc());
            batchSearchS3FileDao.deleteFileByKey(dtoAfterUploadS3.getPeakListS3FileSrc());
            throw new S3DataUploadException("S3 data upload failed");
        }
        dtoAfterUploadS3.setTaskStatus(TaskStatus.NOT_SUBMIT);
        BatchSpectrumSearchModel modelAfterSaveRdb = batchSearchRdbDao.postFileUploadInfo(dtoAfterUploadS3);
        if (modelAfterSaveRdb.getS3Ms2FileSrc() != null) {
            modelAfterSaveRdb.setS3Ms2FileSrc(awsCloudFrontEndpoint + modelAfterSaveRdb.getS3Ms2FileSrc());
        }
        if (modelAfterSaveRdb.getS3PeakListSrc() != null) {
            modelAfterSaveRdb.setS3PeakListSrc(awsCloudFrontEndpoint + modelAfterSaveRdb.getS3PeakListSrc());
        }
        if (modelAfterSaveRdb.getS3ResultsSrc() != null) {
            modelAfterSaveRdb.setS3ResultsSrc(awsCloudFrontEndpoint + modelAfterSaveRdb.getS3ResultsSrc());
        }


        return modelAfterSaveRdb;
    }

    @Override
    @Transactional
    public void postTaskSubmit(BatchSpectrumSearchDto batchSpectrumSearchDto) throws RedisErrorException, QueryParameterException, DatabaseUpdateErrorException, JsonProcessingException {

        //save submit to database
        batchSpectrumSearchDto.setTaskStatus(TaskStatus.SUBMIT_IN_WAITING);
        batchSearchRdbDao.updateTaskInfo(batchSpectrumSearchDto);
        ObjectMapper mapper = new ObjectMapper();
        String taskString = mapper.writeValueAsString(batchSpectrumSearchDto);
        redisTaskQueueService.newTask(taskString);

        //prepare mail VO for sent mail
        RedisSentTaskMailVO redisSentTaskMailVO = new RedisSentTaskMailVO();
        redisSentTaskMailVO.setMailAddress(batchSpectrumSearchDto.getMail());
        redisSentTaskMailVO.setSubject("Task Submit Success");
        redisSentTaskMailVO.setMainText("Your task has been submitted successfully, please wait for the processing result.");
        redisSentTaskMailVO.setPeakListS3FileSrc(batchSpectrumSearchDto.getPeakListS3FileSrc());
        redisSentTaskMailVO.setMs2S3FileSrc(batchSpectrumSearchDto.getMs2S3FileSrc());
        redisSentTaskMailVO.setMs2spectrumDataSource(batchSpectrumSearchDto.getMs2spectrumDataSource());
        redisSentTaskMailVO.setTaskId(batchSpectrumSearchDto.getTaskId());
        redisSentTaskMailVO.setMsTolerance(batchSpectrumSearchDto.getMsTolerance());
        redisSentTaskMailVO.setMsmsTolerance(batchSpectrumSearchDto.getMsmsTolerance());
        redisSentTaskMailVO.setSimilarityTolerance(batchSpectrumSearchDto.getSimilarityTolerance());
        redisSentTaskMailVO.setForwardWeight(batchSpectrumSearchDto.getForwardWeight());
        redisSentTaskMailVO.setReverseWeight(batchSpectrumSearchDto.getReverseWeight());
        redisSentTaskMailVO.setSimilarityAlgorithm(batchSpectrumSearchDto.getSimilarityAlgorithm());
        redisSentTaskMailVO.setIonMode(batchSpectrumSearchDto.getIonMode());
        redisSentTaskMailVO.setTaskStatus(batchSpectrumSearchDto.getTaskStatus());
        redisSentTaskMailVO.setMs1Ms2matchMzTolerance(batchSpectrumSearchDto.getMs1Ms2matchMzTolerance());
        redisSentTaskMailVO.setMs1Ms2matchRtTolerance(batchSpectrumSearchDto.getMs1Ms2matchRtTolerance());
        redisSentTaskMailVO.setTaskDescription(batchSpectrumSearchDto.getTaskDescription());

        String mailString = mapper.writeValueAsString(redisSentTaskMailVO);
        redisMailQueueService.newMail(mailString);

    }

    @Override
    public BatchSpectrumSearchModel getTaskInfoById(int id) throws QueryParameterException, SQLException {

        BatchSpectrumSearchModel batchSpectrumSearchModel =  batchSearchRdbDao.getTaskInfoById(id);
        if (batchSpectrumSearchModel.getS3Ms2FileSrc() != null) {
            batchSpectrumSearchModel.setS3Ms2FileSrc(awsCloudFrontEndpoint + batchSpectrumSearchModel.getS3Ms2FileSrc());
        }
        if (batchSpectrumSearchModel.getS3PeakListSrc() != null) {
            batchSpectrumSearchModel.setS3PeakListSrc(awsCloudFrontEndpoint + batchSpectrumSearchModel.getS3PeakListSrc());
        }
        if (batchSpectrumSearchModel.getS3ResultsSrc() != null) {
            batchSpectrumSearchModel.setS3ResultsSrc(awsCloudFrontEndpoint + batchSpectrumSearchModel.getS3ResultsSrc());
        }

        return batchSpectrumSearchModel;
    }

    @Override
    public List<BatchSpectrumSearchModel> getTaskInfoByParameter(BatchTaskSearchDto batchTaskSearchDto) throws QueryParameterException, SQLException {

        List<BatchSpectrumSearchModel> batchSpectrumSearchModelList = batchSearchRdbDao.getTaskByParameter(batchTaskSearchDto);
        for (int i = 0; i < batchSpectrumSearchModelList.size(); i++) {
            if (batchSpectrumSearchModelList.get(i).getS3Ms2FileSrc() != null) {
                batchSpectrumSearchModelList.get(i).setS3Ms2FileSrc(awsCloudFrontEndpoint + batchSpectrumSearchModelList.get(i).getS3Ms2FileSrc());
            }
            if (batchSpectrumSearchModelList.get(i).getS3PeakListSrc() != null) {
                batchSpectrumSearchModelList.get(i).setS3PeakListSrc(awsCloudFrontEndpoint + batchSpectrumSearchModelList.get(i).getS3PeakListSrc());
            }
            if (batchSpectrumSearchModelList.get(i).getS3ResultsSrc() != null) {
                batchSpectrumSearchModelList.get(i).setS3ResultsSrc(awsCloudFrontEndpoint + batchSpectrumSearchModelList.get(i).getS3ResultsSrc());
            }
        }
        return batchSpectrumSearchModelList;
    }

    @Override
    @Transactional
    public void deleteTaskById(int id) throws QueryParameterException, SQLException, S3DataUploadException {
        BatchSpectrumSearchModel batchSpectrumSearchModel = batchSearchRdbDao.getTaskInfoById(id);
            batchSearchS3FileDao.deleteFileByKey(batchSpectrumSearchModel.getS3Ms2FileSrc());
            batchSearchS3FileDao.deleteFileByKey(batchSpectrumSearchModel.getS3PeakListSrc());

            if (!batchSearchRdbDao.deleteTaskById(id)) {
                throw new S3DataUploadException("delete task failed");
            }
    }

    @Override
    @Transactional
    public void changeTaskStatusToDelete(int id) throws QueryParameterException, SQLException, S3DataUploadException {
        BatchSpectrumSearchModel batchSpectrumSearchModel = batchSearchRdbDao.getTaskInfoById(id);
        batchSearchS3FileDao.deleteFileByKey(batchSpectrumSearchModel.getS3Ms2FileSrc());
        batchSearchS3FileDao.deleteFileByKey(batchSpectrumSearchModel.getS3PeakListSrc());

        if (!batchSearchRdbDao.changeTaskStatusToDelete(id)) {
            throw new S3DataUploadException("change task status to delete is failed");
        }
    }
}
