package service.ms_search_engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.dto.BatchTaskSearchDto;
import service.ms_search_engine.exception.*;
import service.ms_search_engine.model.BatchSpectrumSearchModel;

import java.sql.SQLException;
import java.util.List;

public interface BatchSpectrumSearchService {
    BatchSpectrumSearchModel postFileUpload(BatchSpectrumSearchDto batchSpectrumSearchDto) throws S3DataUploadException, QueryParameterException, DatabaseInsertErrorException;
    void postTaskSubmit(BatchSpectrumSearchDto batchSpectrumSearchDto) throws RedisErrorException, QueryParameterException, DatabaseUpdateErrorException, JsonProcessingException;
    BatchSpectrumSearchModel getTaskInfoById(int id) throws QueryParameterException, SQLException;

    List<BatchSpectrumSearchModel> getTaskInfoByParameter(BatchTaskSearchDto batchTaskSearchDto) throws QueryParameterException, SQLException;
    void deleteTaskById(int id) throws QueryParameterException, SQLException, S3DataUploadException;

    void changeTaskStatusToDelete(int id) throws QueryParameterException, SQLException, S3DataUploadException;

}
