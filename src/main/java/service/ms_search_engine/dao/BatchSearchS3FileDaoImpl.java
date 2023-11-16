package service.ms_search_engine.dao;

import org.springframework.stereotype.Component;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.exception.S3DataUploadException;


@Component
public class BatchSearchS3FileDaoImpl implements BatchSearchS3FileDao{

    @Override
    public BatchSpectrumSearchDto postFileUpload(BatchSpectrumSearchDto batchSpectrumSearchDto) throws S3DataUploadException {
        return null;
    }

    @Override
    public Boolean deleteFileByKey(String key) throws S3DataUploadException {
        return null;
    }
}
