package service.ms_search_engine.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import service.ms_search_engine.model.BatchSpectrumSearchModel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BatchSpectrumSearchRowMapper implements RowMapper<BatchSpectrumSearchModel> {



    @Override
    public BatchSpectrumSearchModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        BatchSpectrumSearchModel batchSpectrumSearchModel = new BatchSpectrumSearchModel();
        batchSpectrumSearchModel.setId(rs.getInt("id"));
        batchSpectrumSearchModel.setS3PeakListSrc(rs.getString("s3_peakList_src"));
        batchSpectrumSearchModel.setS3Ms2FileSrc(rs.getString("s3_ms2file_src"));
        batchSpectrumSearchModel.setS3ResultsSrc(rs.getString("s3_results_src"));
        batchSpectrumSearchModel.setAuthorId(rs.getInt("author_id"));
        batchSpectrumSearchModel.setTaskStatus(rs.getInt("task_status"));
        batchSpectrumSearchModel.setCreateTime(rs.getDate("create_time"));
        batchSpectrumSearchModel.setFinishTime(rs.getDate("finish_time"));
        batchSpectrumSearchModel.setMsTolerance(rs.getDouble("ms_tolerance"));
        batchSpectrumSearchModel.setMsmsTolerance(rs.getDouble("msms_tolerance"));
        batchSpectrumSearchModel.setForwardWeight(rs.getDouble("forward_weight"));
        batchSpectrumSearchModel.setReverseWeight(rs.getDouble("reverse_weight"));
        batchSpectrumSearchModel.setSimilarityAlgorithm(rs.getString("similarity_algorithm"));
        batchSpectrumSearchModel.setIonMode(rs.getString("ion_mode"));
        batchSpectrumSearchModel.setMail(rs.getString("mail"));
        batchSpectrumSearchModel.setSimilarityTolerance(rs.getDouble("similarity_tolerance"));
        batchSpectrumSearchModel.setMs2spectrumDataSource(rs.getString("ms2spectrumDataSource"));
        batchSpectrumSearchModel.setMs1Ms2matchMzTolerance(rs.getDouble("ms1ms2_match_mz_tolerance"));
        batchSpectrumSearchModel.setMs1Ms2matchRtTolerance(rs.getDouble("ms1ms2_match_rt_tolerance"));
        batchSpectrumSearchModel.setTaskDescription(rs.getString("task_description"));

        return batchSpectrumSearchModel;
    }
}
