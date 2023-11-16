package service.ms_search_engine.dao;

import org.springframework.stereotype.Component;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;


@Component
public class BatchSearchS3FileDaoImpl implements BatchSearchS3FileDao{

    @Override
    public BatchSpectrumSearchDto postFileUpload(BatchSpectrumSearchDto batchSpectrumSearchDto) {
        return null;
    }

    @Override
    public Boolean deleteFileByKey(String key) {
        return null;
    }
}
