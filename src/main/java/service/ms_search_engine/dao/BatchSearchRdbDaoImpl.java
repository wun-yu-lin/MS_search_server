package service.ms_search_engine.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import service.ms_search_engine.constant.TaskStatus;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.dto.BatchTaskSearchDto;
import service.ms_search_engine.exception.DatabaseDeleteErrorException;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.DatabaseUpdateErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.BatchSpectrumSearchModel;
import service.ms_search_engine.utility.BatchSpectrumSearchRowMapper;

import java.sql.SQLException;
import java.util.*;

@Component
public class BatchSearchRdbDaoImpl extends BaseDao implements BatchSearchRdbDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BatchSearchRdbDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BatchSpectrumSearchModel postFileUploadInfo(BatchSpectrumSearchDto batchSpectrumSearchDto) throws DatabaseInsertErrorException, QueryParameterException {
        if (batchSpectrumSearchDto.getMs2S3FileSrc() == null ||
                batchSpectrumSearchDto.getPeakListS3FileSrc() == null ||
                batchSpectrumSearchDto.getMs2spectrumDataSource() == null ||
                batchSpectrumSearchDto.getMail() == null

        ) {
            throw new QueryParameterException("parameter is not complete");
        }
        StringBuffer sb = new StringBuffer();
        sb.append(" INSERT INTO ");
        sb.append("  batch_task_info ");
        sb.append(" ( ");
        sb.append("     s3_peakList_src ");
        sb.append("     ,s3_ms2file_src ");
        sb.append("     ,author_id ");
        sb.append("     ,task_status ");
        sb.append("     ,mail ");
        sb.append("     ,ms2spectrumDataSource ");
        sb.append("     ,task_description ");
        sb.append(" ) ");
        sb.append(" VALUES ");
        sb.append(" ( ");
        sb.append("     :s3_peakList_src ");
        sb.append("     ,:s3_ms2file_src ");
        sb.append("     ,:author_id ");
        sb.append("     ,:task_status ");
        sb.append("     ,:mail ");
        sb.append("     ,:ms2spectrumDataSource ");
        sb.append("     ,:taskDescription ");
        sb.append("  ) ");

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("s3_peakList_src", batchSpectrumSearchDto.getPeakListS3FileSrc());
        map.addValue("s3_ms2file_src", batchSpectrumSearchDto.getMs2S3FileSrc());
        map.addValue("author_id", batchSpectrumSearchDto.getAuthorId());
        map.addValue("task_status", batchSpectrumSearchDto.getTaskStatus().getStatusCode());
        map.addValue("mail", batchSpectrumSearchDto.getMail());
        map.addValue("ms2spectrumDataSource", batchSpectrumSearchDto.getMs2spectrumDataSource().name());
        map.addValue("taskDescription", batchSpectrumSearchDto.getTaskDescription());


        int insertStatus = namedParameterJdbcTemplate.update(sb.toString(), map, keyHolder);
        if (insertStatus == 0) {
            throw new DatabaseInsertErrorException("insert failed");
        }
        int insertID = Objects.requireNonNull(keyHolder.getKey()).intValue();
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
        if (batchSpectrumSearchDto.getTaskId() == null ||
                batchSpectrumSearchDto.getMsTolerance() == null ||
                batchSpectrumSearchDto.getMsmsTolerance() == null ||
                batchSpectrumSearchDto.getForwardWeight() == null ||
                batchSpectrumSearchDto.getReverseWeight() == null ||
                batchSpectrumSearchDto.getSimilarityAlgorithm() == null ||
                batchSpectrumSearchDto.getSimilarityTolerance() == null ||
                batchSpectrumSearchDto.getMs1Ms2matchMzTolerance() == null ||
                batchSpectrumSearchDto.getMs1Ms2matchRtTolerance() == null
        ) {
            throw new QueryParameterException("parameter is not complete");
        }


        String sqlStr = "UPDATE ms_search_library.batch_task_info SET task_status=:taskStatus, MS_tolerance=:MSTolerance, s3_results_src=:s3ResultsSrc, " +
                " MSMS_tolerance=:msmsTolerance, forward_weight=:forwardWeight, reverse_weight=:reverseWeight, finish_time=:finishTime,  " +
                " similarity_algorithm=:similarityAlgorithm, ion_mode=:ionMode, similarity_tolerance=:similarityTolerance, mail=:mail, " +
                " ms1ms2_match_mz_tolerance=:ms1Ms2MatchMzTolerance, ms1ms2_match_rt_tolerance=:ms1Ms2MatchRtTolerance, task_description = :taskDescription WHERE id=:taskId;";

        HashMap<String, Object> map = new HashMap<>();
        map.put("taskStatus", batchSpectrumSearchDto.getTaskStatus().getStatusCode());
        map.put("MSTolerance", batchSpectrumSearchDto.getMsTolerance());
        map.put("msmsTolerance", batchSpectrumSearchDto.getMsmsTolerance());
        map.put("forwardWeight", batchSpectrumSearchDto.getForwardWeight());
        map.put("reverseWeight", batchSpectrumSearchDto.getReverseWeight());
        map.put("similarityAlgorithm", batchSpectrumSearchDto.getSimilarityAlgorithm());
        map.put("ionMode", batchSpectrumSearchDto.getIonMode());
        map.put("similarityTolerance", batchSpectrumSearchDto.getSimilarityTolerance());
        map.put("mail", batchSpectrumSearchDto.getMail());
        map.put("taskId", batchSpectrumSearchDto.getTaskId());
        map.put("finishTime", batchSpectrumSearchDto.getFinishTime());
        map.put("ms1Ms2MatchMzTolerance", batchSpectrumSearchDto.getMs1Ms2matchMzTolerance());
        map.put("ms1Ms2MatchRtTolerance", batchSpectrumSearchDto.getMs1Ms2matchRtTolerance());
        map.put("taskDescription", batchSpectrumSearchDto.getTaskDescription());

        if (batchSpectrumSearchDto.getResultPeakListS3FileSrc() == null) {
            map.put("s3ResultsSrc", null);
        } else {
            map.put("s3ResultsSrc", batchSpectrumSearchDto.getResultPeakListS3FileSrc());
        }

        int upDataResult = namedParameterJdbcTemplate.update(sqlStr, map);

        if (upDataResult == 0) {
            throw new DatabaseUpdateErrorException("update task info failed");
        }


        return true;
    }

    @Override
    public BatchSpectrumSearchModel getTaskInfoById(int id) throws QueryParameterException, SQLException {
        String sqlStr = "SELECT id, s3_peakList_src, s3_ms2file_src, s3_results_src, author_id, task_status, create_time, finish_time, MS_tolerance, " +
                "MSMS_tolerance, forward_weight, reverse_weight, similarity_algorithm, ion_mode, mail, similarity_tolerance, ms2spectrumDataSource, " +
                " ms1ms2_match_mz_tolerance, ms1ms2_match_rt_tolerance, task_description" +
                " FROM ms_search_library.batch_task_info bi WHERE bi.id=:id;";

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        return namedParameterJdbcTemplate.queryForObject(sqlStr, map, new BatchSpectrumSearchRowMapper());

    }

    @Override
    public List<BatchSpectrumSearchModel> getTaskByParameter(BatchTaskSearchDto batchTaskSearchDto) throws QueryParameterException, SQLException {
        //check parameter
        if (batchTaskSearchDto.getTaskInit() == null ||
                batchTaskSearchDto.getAuthorId() == null ||
                batchTaskSearchDto.getTaskOffset() == null
        ) {
            throw new QueryParameterException("parameter is not complete");
        }

        String sqlStr = "SELECT id, s3_peakList_src, s3_ms2file_src, s3_results_src, author_id, task_status, create_time, finish_time, MS_tolerance, " +
                "MSMS_tolerance, forward_weight, reverse_weight, similarity_algorithm, ion_mode, mail, similarity_tolerance, ms2spectrumDataSource, " +
                "ms1ms2_match_mz_tolerance, ms1ms2_match_rt_tolerance, task_description FROM ms_search_library.batch_task_info bi WHERE bi.author_id=:authorId and task_status !=:noEqualTaskStatus " +
                " ORDER BY bi.id DESC limit :taskInit, :taskOffset;";

        HashMap<String, Object> map = new HashMap<>();
        map.put("authorId", batchTaskSearchDto.getAuthorId());
        map.put("taskInit", batchTaskSearchDto.getTaskInit());
        map.put("taskOffset", batchTaskSearchDto.getTaskOffset());
        map.put("noEqualTaskStatus", TaskStatus.DELETE.getStatusCode());

        List<BatchSpectrumSearchModel> batchSpectrumSearchModelList = namedParameterJdbcTemplate.query(sqlStr, map, new BatchSpectrumSearchRowMapper());


        return batchSpectrumSearchModelList;
    }

    @Override
    public Boolean deleteTaskById(int id) throws QueryParameterException, DatabaseDeleteErrorException {
        String sqlStr = "DELETE FROM ms_search_library.batch_task_info WHERE id=:id;";
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        int deleteStatus = namedParameterJdbcTemplate.update(sqlStr, map);
        if (deleteStatus == 0) {
            throw new DatabaseDeleteErrorException("delete failed");
        }


        return true;
    }

    @Override
    public Boolean changeTaskStatusToDelete(int id) throws QueryParameterException, SQLException {
        if (id==0){throw new QueryParameterException("Error para");};

        String sqlStr = "UPDATE ms_search_library.batch_task_info SET task_status=:taskStatus WHERE id = :id;";
        HashMap<String, Object> map = new HashMap<>();
        map.put("taskStatus", TaskStatus.DELETE.getStatusCode());
        map.put("id", id);
        int updateStatus = namedParameterJdbcTemplate.update(sqlStr, map);
        if (updateStatus == 0) {
            throw new SQLException("update failed");
        }

        return true;
    }

    @Override
    public BatchSpectrumSearchModel getLastTask() {
        String sqlStr = "select * from ms_search_library.batch_task_info order by id desc limit 0,1;";

        BatchSpectrumSearchModel batchSpectrumSearchModel = namedParameterJdbcTemplate.queryForObject(sqlStr, new HashMap<>(), new BatchSpectrumSearchRowMapper());

        return batchSpectrumSearchModel;
    }
}
