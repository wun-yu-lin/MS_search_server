package service.ms_search_engine.dao;

import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.model.BatchSpectrumSearchModel;

public interface BatchSearchRdbDao{
    BatchSpectrumSearchModel postFileUpload(BatchSpectrumSearchDto batchSpectrumSearchDto);
    BatchSpectrumSearchModel postTaskSubmit(BatchSpectrumSearchDto batchSpectrumSearchDto);
    Boolean updateTaskInfo(BatchSpectrumSearchDto batchSpectrumSearchDto);
    BatchSpectrumSearchDto getTaskInfoById(int id);
    Boolean deleteTaskById(int id);
}
