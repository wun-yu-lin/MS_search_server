package service.ms_search_engine.dao;

import service.ms_search_engine.dto.BatchSpectrumSearchDto;

public interface BatchSearchS3FileDao {
    BatchSpectrumSearchDto postFileUpload(BatchSpectrumSearchDto batchSpectrumSearchDto);
    Boolean deleteFileByKey(String key);

}
