package service.ms_search_engine.dao;

import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import service.ms_search_engine.dto.SpectrumQueryParaDto;
import service.ms_search_engine.model.SpectrumDataModel;
import service.ms_search_engine.utility.SpectrumDataRowMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class SpectrumDaoImpl implements SpectrumDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public SpectrumDataModel getSpectrumByID(int id) {
        String sqlString = "SELECT compound_data_id, compound_classification_id, id, author_id, ms_level, precursor_mz, exact_mass" +
                ", collision_energy, mz_error, last_modify, date_created, data_source, tool_type, instrument, ion_mode, ms2_spectrum, precursor_type  from spectrum_data WHERE id = :id";
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        List<SpectrumDataModel> spectrumDataList = namedParameterJdbcTemplate.query(sqlString, map, new SpectrumDataRowMapper());
        if (spectrumDataList.isEmpty()) {
            return null;
        }
        return spectrumDataList.get(0);
    }

    @Override
    public List<SpectrumDataModel> getSpectrumByParameter(SpectrumQueryParaDto spectrumQueryParaDto) {
        String sqlStringForCount = "SELECT COUNT(*) FROM spectrum_data;";
        Map<String, Object> countMap = new HashMap<>();
        Integer countOfSpectrumData = namedParameterJdbcTemplate.queryForObject(sqlStringForCount, countMap, Integer.class);
        System.out.printf("目前 Spectrum data 總共有 %d 筆數據 ", countOfSpectrumData);


        String sqlString = "SELECT compound_data_id, sd.compound_classification_id, sd.id, author_id, ms_level, precursor_mz, sd.exact_mass" +
                ", collision_energy, mz_error, last_modify, date_created, data_source, tool_type, instrument, ion_mode, ms2_spectrum," +
                " precursor_type, cd.name, cd.formula, cd.inchi_key, cd.inchi, cd.cas, cd.kind, cd.smile from spectrum_data sd left join ms_search_library.compound_data cd on sd.compound_data_id = cd.id where 1=1 ";
        Map<String, Object> map = new HashMap<>();

        //exactMass, maxMz, minMz
        if (spectrumQueryParaDto.getMinPrecursorMz() != null && spectrumQueryParaDto.getMaxPrecursorMz() != null) {
            sqlString = sqlString + " AND `precursor_mz` >= :minPrecursorMz AND `precursor_mz` <= :maxPrecursorMz ";
            map.put("minPrecursorMz", spectrumQueryParaDto.getMinPrecursorMz());
            map.put("maxPrecursorMz", spectrumQueryParaDto.getMaxPrecursorMz());

        }

        if (spectrumQueryParaDto.getMinExactMass() != null && spectrumQueryParaDto.getMaxExactMass() != null) {
            sqlString = sqlString + " AND sd.`exact_mass` >= :minExactMass AND sd.`exact_mass` <= :maxExactMass ";
            map.put("minExactMass", spectrumQueryParaDto.getMinExactMass());
            map.put("maxExactMass", spectrumQueryParaDto.getMaxExactMass());
        }

//        if (spectrumQueryParaDto.getPrecursorType() != null){
//            sqlString = sqlString + " AND sd.precursor_type LIKE :precursorType ";
//            map.put("precursorType", "%"+spectrumQueryParaDto.getPrecursorType()+"%");
//        }

        if (spectrumQueryParaDto.getFormula()!= null) {
            sqlString = sqlString + " AND cd.formula = :formula ";
            map.put("formula", spectrumQueryParaDto.getFormula());
        }

        if (spectrumQueryParaDto.getIonMode() != null) {
            sqlString = sqlString + " AND sd.ion_mode = :ionMode ";
            map.put("ionMode", spectrumQueryParaDto.getIonMode());
        }

        if (spectrumQueryParaDto.getAuthorId() != 0) {
            sqlString = sqlString + " AND sd.author_id = :authorId ";
            map.put("authorId", spectrumQueryParaDto.getAuthorId());
        }

        if (spectrumQueryParaDto.getCompoundName() != null) {
            sqlString = sqlString + " AND cd.name LIKE :compoundName ";
            map.put("compoundName", "%" + spectrumQueryParaDto.getCompoundName() + "%");
        }

        //如果不需要 ms2 spectrum 比對, 再限制數據的比數
        if (spectrumQueryParaDto.getMs2Spectrum() == null
        ) {
            sqlString = sqlString + " LIMIT :spectrumInit,:spectrumOffSet";
            map.put("spectrumInit", spectrumQueryParaDto.getSpectrumInit());
            map.put("spectrumOffSet", spectrumQueryParaDto.getSpectrumOffSet());
        }

        //如需要ms2 spectrum 比對, 先不限制比數，後端比對ms2 spectrum 相似性後


        List<SpectrumDataModel> spectrumDataList = namedParameterJdbcTemplate.query(sqlString, map, new SpectrumDataRowMapper());

        return spectrumDataList;
    }


    private SpectrumDataModel calculateMS2SpectrumSimilarity(SpectrumDataModel spectrumDataModel, String ms2Spectrum) {
        //calculate ms2 spectrum similarity by 2D array cosine similarity

        return spectrumDataModel;
    }
}