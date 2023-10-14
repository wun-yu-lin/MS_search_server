package service.ms_search_engine.utility;

import org.springframework.jdbc.core.RowMapper;
import service.ms_search_engine.model.SpectrumDataModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class SpectrumDataRowMapper implements RowMapper<SpectrumDataModel> {
    @Override
    public SpectrumDataModel mapRow(ResultSet rs, int rowNum) throws SQLException {

        SpectrumDataModel spectrumData = new SpectrumDataModel();
        spectrumData.setCompoundDataId(rs.getInt("compound_data_id"));
        spectrumData.setCompoundClassificationId(rs.getInt("compound_classification_id"));
        spectrumData.setId(rs.getInt("id"));
        spectrumData.setAuthorId(rs.getInt("author_id"));
        spectrumData.setMsLevel(rs.getInt("ms_level"));
        spectrumData.setPrecursorMz(rs.getDouble("precursor_mz"));
        spectrumData.setExactMass(rs.getDouble("exact_mass"));
        spectrumData.setCollisionEnergy(rs.getString("collision_energy"));
        spectrumData.setMzError(rs.getDouble("mz_error"));
        spectrumData.setLastModify(rs.getTimestamp("last_modify"));
        spectrumData.setDateCreated(rs.getTimestamp("date_created"));

        //原本為字串，先拆為ArrayList，並將賦予給spectrumData內部屬性
        ArrayList<String> dataSourceArrList = new ArrayList<String>(Arrays.asList(rs.getString("data_source").split("','")));
        spectrumData.setDataSourceArrayList(dataSourceArrList);
        spectrumData.setToolType(rs.getString("tool_type"));
        spectrumData.setInstrument(rs.getString("instrument"));
        spectrumData.setIonMode(rs.getString("ion_mode"));
        spectrumData.setMs2Spectrum(rs.getString("ms2_spectrum"));
        spectrumData.setPrecursorType(rs.getString("precursor_type"));

        return spectrumData;
    }
}
