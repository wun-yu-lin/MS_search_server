package service.ms_search_engine.dao;

import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.dto.BatchTaskSearchDto;
import service.ms_search_engine.exception.DatabaseDeleteErrorException;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.DatabaseUpdateErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.BatchSpectrumSearchModel;

import java.sql.SQLException;
import java.util.List;

public interface BatchSearchRdbDao{
    BatchSpectrumSearchModel postFileUploadInfo(BatchSpectrumSearchDto batchSpectrumSearchDto) throws DatabaseInsertErrorException, QueryParameterException;
    BatchSpectrumSearchModel postTaskSubmit(BatchSpectrumSearchDto batchSpectrumSearchDto) throws DatabaseInsertErrorException, QueryParameterException;
    void updateTaskInfo(BatchSpectrumSearchDto batchSpectrumSearchDto) throws DatabaseUpdateErrorException, QueryParameterException;
    BatchSpectrumSearchModel getTaskInfoById(int id) throws QueryParameterException, SQLException;
    List<BatchSpectrumSearchModel> getTaskByParameter(BatchTaskSearchDto batchTaskSearchDto) throws QueryParameterException, SQLException;
    Boolean deleteTaskById(int id) throws QueryParameterException, DatabaseDeleteErrorException;

    Boolean changeTaskStatusToDelete(int id) throws QueryParameterException, SQLException;

    Integer getLastTask();
}
