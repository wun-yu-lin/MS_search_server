package service.ms_search_engine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import service.ms_search_engine.dao.BatchSearchRdbDao;
import service.ms_search_engine.dao.BatchSearchS3FileDao;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.dto.BatchTaskSearchDto;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.exception.S3DataUploadException;
import service.ms_search_engine.model.BatchSpectrumSearchModel;

import java.sql.SQLException;
import java.util.List;

@Component
public class BatchSpectrumSearchServiceImpl implements BatchSpectrumSearchService {

    @Value("${aws.cloudFront.endpoint}")
    private String awsCloudFrontEndpoint;

    private final BatchSearchRdbDao batchSearchRdbDao;
    private final BatchSearchS3FileDao batchSearchS3FileDao;

    @Autowired
    public BatchSpectrumSearchServiceImpl(BatchSearchRdbDao batchSearchRdbDao, BatchSearchS3FileDao batchSearchS3FileDao) {
        this.batchSearchRdbDao = batchSearchRdbDao;
        this.batchSearchS3FileDao = batchSearchS3FileDao;
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
    public BatchSpectrumSearchModel postTaskSubmit(BatchSpectrumSearchDto batchSpectrumSearchDto) {
        return null;
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
