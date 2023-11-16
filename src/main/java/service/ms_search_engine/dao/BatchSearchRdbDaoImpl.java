package service.ms_search_engine.dao;

import org.springframework.stereotype.Component;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.model.BatchSpectrumSearchModel;

@Component
public class BatchSearchRdbDaoImpl implements BatchSearchRdbDao{
    @Override
    public BatchSpectrumSearchModel postFileUpload(BatchSpectrumSearchDto batchSpectrumSearchDto) {
        return null;
    }

    @Override
    public BatchSpectrumSearchModel postTaskSubmit(BatchSpectrumSearchDto batchSpectrumSearchDto) {
        return null;
    }

    @Override
    public Boolean updateTaskInfo(BatchSpectrumSearchDto batchSpectrumSearchDto) {
        return null;
    }

    @Override
    public BatchSpectrumSearchDto getTaskInfoById(int id) {
        return null;
    }

    @Override
    public Boolean deleteTaskById(int id) {
        return null;
    }
}
