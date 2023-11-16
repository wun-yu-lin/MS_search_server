package service.ms_search_engine.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import service.ms_search_engine.constant.TaskStatus;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.exception.DatabaseDeleteErrorException;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.DatabaseUpdateErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.BatchSpectrumSearchModel;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

@Component
public class BatchSearchRdbDaoImpl implements BatchSearchRdbDao{

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public BatchSearchRdbDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public BatchSpectrumSearchModel postFileUploadInfo(BatchSpectrumSearchDto batchSpectrumSearchDto) throws DatabaseInsertErrorException, QueryParameterException {
        if (batchSpectrumSearchDto.getMs2S3FileSrc()==null ||
            batchSpectrumSearchDto.getPeakListS3FileSrc()==null ||
            batchSpectrumSearchDto.getMs2spectrumDataSource()==null ||
            batchSpectrumSearchDto.getMail()==null
//            batchSpectrumSearchDto.getAuthorId()==null
        ){
            throw new QueryParameterException("parameter is not complete");
        }
        String sqlString ="INSERT INTO ms_search_library.batch_task_info (s3_peakList_src, s3_ms2file_src, author_id, " +
                "task_status, mail, ms2spectrumDataSource) " +
                "VALUES (:s3_peakList_src, :s3_ms2file_src, :author_id, :task_status, :mail, :ms2spectrumDataSource);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("s3_peakList_src", batchSpectrumSearchDto.getPeakListS3FileSrc());
        map.addValue("s3_ms2file_src", batchSpectrumSearchDto.getMs2S3FileSrc());
        map.addValue("author_id", batchSpectrumSearchDto.getAuthorId());
        map.addValue("task_status", TaskStatus.NOT_SUBMIT.getStatusCode());
        map.addValue("mail", batchSpectrumSearchDto.getMail());
        map.addValue("ms2spectrumDataSource", batchSpectrumSearchDto.getMs2spectrumDataSource().name());


        int insertStatus = namedParameterJdbcTemplate.update(sqlString, map,keyHolder);
        if (insertStatus == 0){
            throw new DatabaseInsertErrorException("insert failed");
        }
        int insertID =  Objects.requireNonNull(keyHolder.getKey()).intValue();
        BatchSpectrumSearchModel batchSpectrumSearchModel = new BatchSpectrumSearchModel();
        batchSpectrumSearchModel.setId(insertID);
        batchSpectrumSearchModel.setS3PeakListSrc(batchSpectrumSearchDto.getPeakListS3FileSrc());
        batchSpectrumSearchModel.setS3Ms2FileSrc(batchSpectrumSearchDto.getMs2S3FileSrc());
        batchSpectrumSearchModel.setAuthorId(batchSpectrumSearchDto.getAuthorId());
        batchSpectrumSearchModel.setTaskStatus(TaskStatus.NOT_SUBMIT.getStatusCode());
        batchSpectrumSearchModel.setMail(batchSpectrumSearchDto.getMail());
        batchSpectrumSearchModel.setMs2spectrumDataSource(batchSpectrumSearchDto.getMs2spectrumDataSource().name());


        return batchSpectrumSearchModel;
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
