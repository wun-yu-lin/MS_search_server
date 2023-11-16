package service.ms_search_engine.dao;

import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.exception.S3DataUploadException;

public interface BatchSearchS3FileDao {
    BatchSpectrumSearchDto postFileUpload(BatchSpectrumSearchDto batchSpectrumSearchDto) throws S3DataUploadException;
    Boolean deleteFileByKey(String key) throws S3DataUploadException;

}
