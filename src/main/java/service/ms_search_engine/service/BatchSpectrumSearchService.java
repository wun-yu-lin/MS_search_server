package service.ms_search_engine.service;

import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.model.BatchSpectrumSearchModel;

public interface BatchSpectrumSearchService {
    BatchSpectrumSearchModel postFileUpload(BatchSpectrumSearchDto batchSpectrumSearchDto);
    BatchSpectrumSearchModel postTaskSubmit(BatchSpectrumSearchDto batchSpectrumSearchDto);
    BatchSpectrumSearchModel getTaskInfoById(int id);
    Boolean deleteTaskById(int id);

}
