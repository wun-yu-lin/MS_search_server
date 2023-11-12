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
        MS2spectrumDataTransFormation ms2spectrumDataTransFormation = new MS2spectrumDataTransFormation();

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
        ArrayList<String> dataSourceArrList = new ArrayList<String>(Arrays.asList(rs.getString("data_source").replace("[","").replace("]","") .replaceAll("'", "").split(", ")));

        spectrumData.setDataSourceArrayList(dataSourceArrList);
        spectrumData.setToolType(rs.getString("tool_type"));
        spectrumData.setInstrument(rs.getString("instrument"));
        spectrumData.setIonMode(rs.getString("ion_mode"));
        spectrumData.setMs2Spectrum(rs.getString("ms2_spectrum"));
        spectrumData.setMs2SpectrumList(ms2spectrumDataTransFormation.ms2SpectrumStringToNestedArray(rs.getString("ms2_spectrum")));
        spectrumData.setPrecursorType(rs.getString("precursor_type"));


        //join data from compound_data table
        spectrumData.setFormula(rs.getString("formula"));
        spectrumData.setName(rs.getString("name"));
        spectrumData.setInChiKey(rs.getString("inchi_key"));
        spectrumData.setInChi(rs.getString("inchi"));
        spectrumData.setCas(rs.getString("cas"));
        spectrumData.setKind(rs.getString("kind"));
        spectrumData.setSmile(rs.getString("smile"));


        return spectrumData;
    }
}
