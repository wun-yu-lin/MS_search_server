package service.ms_search_engine.service;

import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.dto.BatchTaskSearchDto;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.exception.S3DataUploadException;
import service.ms_search_engine.model.BatchSpectrumSearchModel;

import java.sql.SQLException;
import java.util.List;

public interface BatchSpectrumSearchService {
    BatchSpectrumSearchModel postFileUpload(BatchSpectrumSearchDto batchSpectrumSearchDto) throws S3DataUploadException, QueryParameterException, DatabaseInsertErrorException;
    BatchSpectrumSearchModel postTaskSubmit(BatchSpectrumSearchDto batchSpectrumSearchDto);
    BatchSpectrumSearchModel getTaskInfoById(int id) throws QueryParameterException, SQLException;

    List<BatchSpectrumSearchModel> getTaskInfoByParameter(BatchTaskSearchDto batchTaskSearchDto) throws QueryParameterException, SQLException;
    Boolean deleteTaskById(int id) throws QueryParameterException, SQLException, S3DataUploadException;

}
