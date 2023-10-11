package service.ms_search_engine.jdbcTest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;
import service.ms_search_engine.model.SpectrumData;

import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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


        String sql_string = "INSERT INTO spectrum_data(compound_data_id, compound_classification_id, author_id, ms_level, precursor_mz, exact_mass,"+
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
        namedParameterJdbcTemplate.update(sql_string, new MapSqlParameterSource(map), keyHolder);
        }catch (RuntimeException runtimeException){
            throw new RuntimeException(runtimeException);
        }
        int insertedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        return ResponseEntity.status(200).body("Success insert into database, id:" + insertedId);
    }
}
