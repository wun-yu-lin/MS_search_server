package service.ms_search_engine.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import service.ms_search_engine.model.SpectrumDataModel;
import service.ms_search_engine.utility.SpectrumDataRowMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class SpectrumDaoImpl implements SpectrumDao{

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public SpectrumDataModel getSpectrumByID(int id) {
        String sqlString = "SELECT compound_data_id, compound_classification_id, id, author_id, ms_level, precursor_mz, exact_mass" +
                ", collision_energy, mz_error, last_modify, date_created, data_source, tool_type, instrument, ion_mode, ms2_spectrum, precursor_type  from spectrum_data WHERE id = :id";
        Map<String,Object> map = new HashMap<>();
        map.put("id", id);
        List<SpectrumDataModel> spectrumDataList  = namedParameterJdbcTemplate.query(sqlString, map, new SpectrumDataRowMapper());
        if (spectrumDataList.isEmpty()){
            return null;
        }
        return spectrumDataList.get(0);
    }

    @Override
    public List<SpectrumDataModel> getSpectrumByParameter(int spectrumNum)  {
        String sqlStringForCount = "SELECT COUNT(*) FROM spectrum_data;";
        Map<String, Object> countMap = new HashMap<>();
        Integer countOfSpectrumData = namedParameterJdbcTemplate.queryForObject(sqlStringForCount, countMap, Integer.class);
        System.out.printf("目前 Spectrum data 總共有 %d 筆數據 ", countOfSpectrumData);


        String sqlString = "SELECT compound_data_id, compound_classification_id, id, author_id, ms_level, precursor_mz, exact_mass" +
                ", collision_energy, mz_error, last_modify, date_created, data_source, tool_type, instrument, ion_mode, ms2_spectrum, precursor_type  from spectrum_data limit 0,500;";
        Map<String,Object> map = new HashMap<>();
        List<SpectrumDataModel> spectrumDataList  = namedParameterJdbcTemplate.query(sqlString, map, new SpectrumDataRowMapper());

        return spectrumDataList;
    }
}
