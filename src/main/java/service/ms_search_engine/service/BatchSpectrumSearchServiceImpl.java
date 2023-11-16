package service.ms_search_engine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.ms_search_engine.dao.BatchSearchRdbDao;
import service.ms_search_engine.dao.BatchSearchS3FileDao;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.exception.S3DataUploadException;
import service.ms_search_engine.model.BatchSpectrumSearchModel;

@Component
public class BatchSpectrumSearchServiceImpl implements BatchSpectrumSearchService{

    private final BatchSearchRdbDao batchSearchRdbDao;
    private final BatchSearchS3FileDao batchSearchS3FileDao;

    @Autowired
    public BatchSpectrumSearchServiceImpl(BatchSearchRdbDao batchSearchRdbDao, BatchSearchS3FileDao batchSearchS3FileDao) {
        this.batchSearchRdbDao = batchSearchRdbDao;
        this.batchSearchS3FileDao = batchSearchS3FileDao;
    }

    @Override
    public BatchSpectrumSearchModel postFileUpload(BatchSpectrumSearchDto batchSpectrumSearchDto) throws S3DataUploadException, QueryParameterException, DatabaseInsertErrorException {
        BatchSpectrumSearchDto dtoAfterUploadS3 =  batchSearchS3FileDao.postFileUpload(batchSpectrumSearchDto);
        if (dtoAfterUploadS3.getMs2S3FileSrc()==null || dtoAfterUploadS3.getPeakListFile() == null){
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
    public BatchSpectrumSearchModel getTaskInfoById(int id) {
        return null;
    }

    @Override
    public Boolean deleteTaskById(int id) {
        return null;
    }
}
