package service.ms_search_engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import service.ms_search_engine.dao.BatchSearchRdbDao;
import service.ms_search_engine.dao.BatchSearchS3FileDao;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.dto.BatchTaskSearchDto;
import service.ms_search_engine.exception.*;
import service.ms_search_engine.model.BatchSpectrumSearchModel;
import service.ms_search_engine.redisService.RedisTaskQueueService;
import service.ms_search_engine.redisService.RedisUtil;

import java.sql.SQLException;
import java.util.List;

@Component
public class BatchSpectrumSearchServiceImpl implements BatchSpectrumSearchService {

    @Value("${aws.cloudFront.endpoint}")
    private String awsCloudFrontEndpoint;

    private final BatchSearchRdbDao batchSearchRdbDao;
    private final BatchSearchS3FileDao batchSearchS3FileDao;
    private  final RedisTaskQueueService redisTaskQueueService;

    @Autowired
    public BatchSpectrumSearchServiceImpl(BatchSearchRdbDao batchSearchRdbDao,
                                          BatchSearchS3FileDao batchSearchS3FileDao,
                                          RedisTaskQueueService redisTaskQueueService) {
        this.batchSearchRdbDao = batchSearchRdbDao;
        this.batchSearchS3FileDao = batchSearchS3FileDao;
        this.redisTaskQueueService = redisTaskQueueService;
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
        BatchSpectrumSearchModel modelAfterSaveRdb = batchSearchRdbDao.postFileUploadInfo(dtoAfterUploadS3);

        return modelAfterSaveRdb;
    }

    @Override
    @Transactional
    public Boolean postTaskSubmit(BatchSpectrumSearchDto batchSpectrumSearchDto) throws RedisErrorException, QueryParameterException, DatabaseUpdateErrorException, JsonProcessingException {

        //save submit to database
        Boolean isRdbUpdateSuccess = batchSearchRdbDao.updateTaskInfo(batchSpectrumSearchDto);
        if (!isRdbUpdateSuccess) {
            throw new DatabaseUpdateErrorException("update task info failed");
        }
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(batchSpectrumSearchDto);
        redisTaskQueueService.newTask(jsonString);
        String taskString = redisTaskQueueService.getAndPopLastTask();
        BatchSpectrumSearchDto batchSpectrumSearchDto1 = mapper.readValue(taskString, BatchSpectrumSearchDto.class);




        return true;
    }

    @Override
    public BatchSpectrumSearchModel getTaskInfoById(int id) throws QueryParameterException, SQLException {
        return batchSearchRdbDao.getTaskInfoById(id);
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
    public Boolean deleteTaskById(int id) throws QueryParameterException, SQLException, S3DataUploadException {
        BatchSpectrumSearchModel batchSpectrumSearchModel = batchSearchRdbDao.getTaskInfoById(id);
            batchSearchS3FileDao.deleteFileByKey(batchSpectrumSearchModel.getS3Ms2FileSrc());
            batchSearchS3FileDao.deleteFileByKey(batchSpectrumSearchModel.getS3PeakListSrc());

            if (!batchSearchRdbDao.deleteTaskById(id)) {
                throw new S3DataUploadException("delete task failed");
            }
        return true;
    }
}
