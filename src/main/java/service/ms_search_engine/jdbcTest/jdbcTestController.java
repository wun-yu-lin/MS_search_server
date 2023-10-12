package service.ms_search_engine.jdbcTest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;
import service.ms_search_engine.Spectrum;
import service.ms_search_engine.model.SpectrumData;
import service.ms_search_engine.utility.SpectrumDataRowMapper;

import javax.sound.sampled.EnumControl;
import java.sql.Array;
import java.sql.Struct;
import java.util.*;

@RestController
@RequestMapping("/jdbcTest")
public class jdbcTestController {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @PostMapping("/insertSpectrum")
    public ResponseEntity<String> insertSpectrum(
            @RequestBody SpectrumData spectrumData
            ){
        Map<String, Object> map = new HashMap<>();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try{


        String sqlString = "INSERT INTO spectrum_data(compound_data_id, compound_classification_id, author_id, ms_level, precursor_mz, exact_mass,"+
                            " collision_energy, mz_error, data_source, tool_type, instrument, ion_mode, ms2_spectrum, precursor_type)" +
                            "VALUES (:compound_data_id, :compound_classification_id, :author_id, :ms_level, :precursor_mz, :exact_mass, :collision_energy,"+
                            ":mz_error, :data_source, :tool_type, :instrument, :ion_mode, :ms2_spectrum, :precursor_type)";
        map.put("compound_data_id", spectrumData.getCompoundDataId());
        map.put("compound_classification_id", spectrumData.getCompoundClassificationId());
        map.put("author_id", spectrumData.getAuthorId());
        map.put("ms_level", spectrumData.getMsLevel());
        map.put("precursor_mz", spectrumData.getPrecursorMz());
        map.put("exact_mass", spectrumData.getExactMass());
        map.put("collision_energy", spectrumData.getCollisionEnergy());
        map.put("mz_error", spectrumData.getMzError());
        map.put("data_source", spectrumData.getDataSourceArrayList().toString());
        map.put("tool_type", spectrumData.getToolType());
        map.put("instrument", spectrumData.getInstrument());
        map.put("ion_mode", spectrumData.getIonMode());
        map.put("ms2_spectrum", spectrumData.getMs2Spectrum());
        map.put("precursor_type", spectrumData.getPrecursorType());
        namedParameterJdbcTemplate.update(sqlString, new MapSqlParameterSource(map), keyHolder);
        }catch (RuntimeException runtimeException){
            throw new RuntimeException(runtimeException);
        }
        int insertedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        return ResponseEntity.status(HttpStatus.OK.value()).body("Success insert into database, id:" + insertedId);
    }


    @PostMapping("/insertBatchSpectrum")
    public ResponseEntity<String> insertBatchSpectrum(@RequestBody List<SpectrumData> spectrumList){
        String sqlString = "INSERT INTO spectrum_data(compound_data_id, compound_classification_id, author_id, ms_level, precursor_mz, exact_mass,"+
                " collision_energy, mz_error, data_source, tool_type, instrument, ion_mode, ms2_spectrum, precursor_type)" +
                "VALUES (:compound_data_id, :compound_classification_id, :author_id, :ms_level, :precursor_mz, :exact_mass, :collision_energy,"+
                ":mz_error, :data_source, :tool_type, :instrument, :ion_mode, :ms2_spectrum, :precursor_type)";
        MapSqlParameterSource[] parameterSources = new MapSqlParameterSource[spectrumList.size()];

        int spectrumListSize = spectrumList.size();

        for (int i = 0; i<spectrumListSize; i++){
            SpectrumData spectrumData = spectrumList.get(i);

            parameterSources[i] = new MapSqlParameterSource();

            parameterSources[i].addValue("compound_data_id", spectrumData.getCompoundDataId());
            parameterSources[i].addValue("compound_classification_id", spectrumData.getCompoundClassificationId());
            parameterSources[i].addValue("author_id", spectrumData.getAuthorId());
            parameterSources[i].addValue("ms_level", spectrumData.getMsLevel());
            parameterSources[i].addValue("precursor_mz", spectrumData.getPrecursorMz());
            parameterSources[i].addValue("exact_mass", spectrumData.getExactMass());
            parameterSources[i].addValue("collision_energy", spectrumData.getCollisionEnergy());
            parameterSources[i].addValue("mz_error", spectrumData.getMzError());
            parameterSources[i].addValue("data_source", spectrumData.getDataSourceArrayList().toString());
            parameterSources[i].addValue("tool_type", spectrumData.getToolType());
            parameterSources[i].addValue("instrument", spectrumData.getInstrument());
            parameterSources[i].addValue("ion_mode", spectrumData.getIonMode());
            parameterSources[i].addValue("ms2_spectrum", spectrumData.getMs2Spectrum());
            parameterSources[i].addValue("precursor_type", spectrumData.getPrecursorType());
        }
        namedParameterJdbcTemplate.batchUpdate(sqlString, parameterSources);

        return ResponseEntity.status(HttpStatus.OK.value()).body("successfully insert batch spectrum data");
    }

    @DeleteMapping("/deleteSpectrum/{id}")
    public ResponseEntity<String> deleteSpectrumByID(@PathVariable int id){
        String sqlString = "DELETE FROM  spectrum_data WHERE id = :spectrumID";
        Map<String,Object> map = new HashMap<>();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        map.put("spectrumID", id);
        namedParameterJdbcTemplate.update(sqlString, new MapSqlParameterSource(map), keyHolder);
        return ResponseEntity.status(HttpStatus.OK.value()).body("執行 delete sql");
    }


    @GetMapping("/spectrum")
    public ResponseEntity<List<SpectrumData>> getSpectrum(){
        String sqlStringForCount = "SELECT COUNT(*) FROM spectrum_data;";
        Map<String, Object> countMap = new HashMap<>();
        Integer countOfSpectrumData = namedParameterJdbcTemplate.queryForObject(sqlStringForCount, countMap, Integer.class);
        System.out.printf("目前 Spectrum data 總共有 %d 筆數據 ", countOfSpectrumData);


        String sqlString = "SELECT compound_data_id, compound_classification_id, id, author_id, ms_level, precursor_mz, exact_mass" +
        ", collision_energy, mz_error, last_modify, date_created, data_source, tool_type, instrument, ion_mode, ms2_spectrum, precursor_type  from spectrum_data limit 0,500;";
        Map<String,Object> map = new HashMap<>();
        List<SpectrumData> spectrumDataList  = namedParameterJdbcTemplate.query(sqlString, map, new SpectrumDataRowMapper());
        return ResponseEntity.status(200).body(spectrumDataList);
    }

    @GetMapping("/spectrum/{id}")
    public ResponseEntity<SpectrumData> getSpectrumByID(@PathVariable int id){
        String sqlString = "SELECT compound_data_id, compound_classification_id, id, author_id, ms_level, precursor_mz, exact_mass" +
                ", collision_energy, mz_error, last_modify, date_created, data_source, tool_type, instrument, ion_mode, ms2_spectrum, precursor_type  from spectrum_data WHERE id = :id";
        Map<String,Object> map = new HashMap<>();
        map.put("id", id);
        List<SpectrumData> spectrumDataList  = namedParameterJdbcTemplate.query(sqlString, map, new SpectrumDataRowMapper());
        if (spectrumDataList.isEmpty()){
            return ResponseEntity.status(200).body(null);
        }

        return ResponseEntity.status(200).body(spectrumDataList.get(0));
    }

}
