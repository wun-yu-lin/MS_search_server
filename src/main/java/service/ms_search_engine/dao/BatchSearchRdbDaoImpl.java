package service.ms_search_engine.dao;

import org.springframework.stereotype.Component;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.exception.DatabaseDeleteErrorException;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.DatabaseUpdateErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.BatchSpectrumSearchModel;

import java.sql.SQLException;

@Component
public class BatchSearchRdbDaoImpl implements BatchSearchRdbDao{

    @Override
    public BatchSpectrumSearchModel postFileUpload(BatchSpectrumSearchDto batchSpectrumSearchDto) throws DatabaseInsertErrorException, QueryParameterException {
        return null;
    }

    @Override
    public BatchSpectrumSearchModel postTaskSubmit(BatchSpectrumSearchDto batchSpectrumSearchDto) throws DatabaseInsertErrorException, QueryParameterException {
        return null;
    }

    @Override
    public Boolean updateTaskInfo(BatchSpectrumSearchDto batchSpectrumSearchDto) throws DatabaseUpdateErrorException, QueryParameterException {
        return null;
    }

    @Override
    public BatchSpectrumSearchDto getTaskInfoById(int id) throws QueryParameterException, SQLException {
        return null;
    }

    @Override
    public Boolean deleteTaskById(int id) throws QueryParameterException, DatabaseDeleteErrorException {
        return null;
    }
}
