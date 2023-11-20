package service.ms_search_engine.dao;

import org.springframework.core.io.UrlResource;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.exception.S3DataDownloadException;
import service.ms_search_engine.exception.S3DataUploadException;
import service.ms_search_engine.model.BatchSpectrumSearchModel;

import java.io.IOException;

public interface BatchSearchS3FileDao {
    BatchSpectrumSearchDto postFileUpload(BatchSpectrumSearchDto batchSpectrumSearchDto) throws S3DataUploadException;
    Boolean deleteFileByKey(String key) throws S3DataUploadException;
    String generateFileNameByUUID();
    UrlResource downloadFileByFileName(String fileName) throws S3DataDownloadException, IOException;

}
