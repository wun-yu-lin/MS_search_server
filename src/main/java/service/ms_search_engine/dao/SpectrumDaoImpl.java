package service.ms_search_engine.dao;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import service.ms_search_engine.dto.SpectrumQueryParaDto;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.SpectrumDataModel;
import service.ms_search_engine.utility.MS2spectrumDataTransFormation;
import service.ms_search_engine.utility.MS2spectrumSimilarityCalculator;
import service.ms_search_engine.utility.SpectrumDataRowMapper;

import java.util.*;


@Component
public class SpectrumDaoImpl implements SpectrumDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    JdbcTemplate jdbcTemplate;


    @Override
    public SpectrumDataModel getSpectrumByID(int id) {
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append("     compound_data_id, ");
        sb.append("     sd.compound_classification_id, ");
        sb.append("     sd.id, ");
        sb.append("     sd.author_id, ");
        sb.append("     sd.ms_level, ");
        sb.append("     sd.precursor_mz, ");
        sb.append("     sd.exact_mass, ");
        sb.append("     sd.collision_energy, ");
        sb.append("     sd.mz_error, ");
        sb.append("     sd.last_modify, ");
        sb.append("     sd.date_created, ");
        sb.append("     sd.data_source, ");
        sb.append("     sd.tool_type, ");
        sb.append("     sd.instrument, ");
        sb.append("     sd.ion_mode, ");
        sb.append("     sd.ms2_spectrum, ");
        sb.append("     sd.precursor_type, ");
        sb.append("     cd.name, ");
        sb.append("     cd.formula, ");
        sb.append("     cd.inchi_key, ");
        sb.append("     cd.inchi, ");
        sb.append("     cd.cas, ");
        sb.append("     cd.kind, ");
        sb.append("     cd.smile ");
        sb.append(" FROM spectrum_data sd ");
        sb.append(" LEFT JOIN ms_search_library.compound_data cd ON sd.compound_data_id = cd.id ");
        sb.append(" WHERE sd.id = :id ");
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        List<SpectrumDataModel> spectrumDataList = namedParameterJdbcTemplate.query(sb.toString(), map, new SpectrumDataRowMapper());
        if (spectrumDataList.isEmpty()) {
            return null;
        }
        return spectrumDataList.get(0);
    }

    @Override
    public List<SpectrumDataModel> getSpectrumByParameter(SpectrumQueryParaDto spectrumQueryParaDto) throws QueryParameterException {
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append("     sd.compound_data_id, ");
        sb.append("     sd.compound_classification_id, ");
        sb.append("     sd.id, ");
        sb.append("     sd.author_id, ");
        sb.append("     sd.ms_level, ");
        sb.append("     sd.precursor_mz, ");
        sb.append("     sd.exact_mass, ");
        sb.append("     sd.collision_energy, ");
        sb.append("     sd.mz_error, ");
        sb.append("     sd.last_modify, ");
        sb.append("     sd.date_created, ");
        sb.append("     sd.data_source, ");
        sb.append("     sd.tool_type, ");
        sb.append("     sd.instrument, ");
        sb.append("     sd.ion_mode, ");
        sb.append("     sd.ms2_spectrum, ");
        sb.append("     sd.precursor_type, ");
        sb.append("     cd.name, ");
        sb.append("     cd.formula, ");
        sb.append("     cd.inchi_key, ");
        sb.append("     cd.inchi, ");
        sb.append("     cd.cas, ");
        sb.append("     cd.kind, ");
        sb.append("     cd.smile ");
        sb.append(" FROM spectrum_data sd ");
        sb.append(" LEFT JOIN ms_search_library.compound_data cd ON sd.compound_data_id = cd.id ");
        sb.append(" WHERE 1=1 ");

        Map<String, Object> map = new HashMap<>();

        //exactMass, maxMz, minMz
        if (spectrumQueryParaDto.getMinPrecursorMz() != null && spectrumQueryParaDto.getMaxPrecursorMz() != null) {
            sb.append(" AND sd.precursor_mz >= :minPrecursorMz AND sd.precursor_mz <= :maxPrecursorMz ");
            map.put("minPrecursorMz", spectrumQueryParaDto.getMinPrecursorMz());
            map.put("maxPrecursorMz", spectrumQueryParaDto.getMaxPrecursorMz());

        }

        if (spectrumQueryParaDto.getMinExactMass() != null && spectrumQueryParaDto.getMaxExactMass() != null) {
            sb.append(" AND sd.exact_mass >= :minExactMass AND sd.exact_mass <= :maxExactMass ");
            map.put("minExactMass", spectrumQueryParaDto.getMinExactMass());
            map.put("maxExactMass", spectrumQueryParaDto.getMaxExactMass());
        }

//        if (spectrumQueryParaDto.getPrecursorType() != null){
//            sqlString = sqlString + " AND sd.precursor_type LIKE :precursorType ";
//            map.put("precursorType", "%"+spectrumQueryParaDto.getPrecursorType()+"%");
//        }

        if (spectrumQueryParaDto.getFormula() != null) {
            sb.append(" AND cd.formula = :formula ");
            map.put("formula", spectrumQueryParaDto.getFormula());
        }

        if (spectrumQueryParaDto.getIonMode() != null) {
            sb.append(" AND sd.ion_mode = :ionMode ");
            map.put("ionMode", spectrumQueryParaDto.getIonMode());
        }

        if (spectrumQueryParaDto.getAuthorId() != 0) {
            sb.append(" AND sd.author_id = :authorId ");
            map.put("authorId", spectrumQueryParaDto.getAuthorId());
        }

        if (spectrumQueryParaDto.getCompoundName() != null) {
            sb.append(" AND cd.name LIKE :compoundName ");
            map.put("compoundName", "['" + spectrumQueryParaDto.getCompoundName() + "%");
        }

        //如果不需要 ms2 spectrum 比對, 再限制數據的比數
        if (spectrumQueryParaDto.getMs2Spectrum() == null) {
            sb.append(" LIMIT :spectrumInit,:spectrumOffSet ");
            map.put("spectrumInit", spectrumQueryParaDto.getSpectrumInit());
            map.put("spectrumOffSet", spectrumQueryParaDto.getSpectrumOffSet());
        } else {
            if (spectrumQueryParaDto.getMaxPrecursorMz() == null || spectrumQueryParaDto.getMinPrecursorMz() == null) {
                throw new QueryParameterException("MaxPrecursorMz or MinPrecursorMz can not be null when ms2 spectrum similarity is needed");
            }
        }

        List<SpectrumDataModel> spectrumDataList = namedParameterJdbcTemplate.query(sb.toString(), map, new SpectrumDataRowMapper());
        if (spectrumQueryParaDto.getMs2Spectrum() != null) {

            MS2spectrumSimilarityCalculator ms2spectrumSimilarityCalculator = new MS2spectrumSimilarityCalculator();
            MS2spectrumDataTransFormation ms2spectrumDataTransFormation = new MS2spectrumDataTransFormation();
            final List<Double[]> expMs2Spectrum = ms2spectrumDataTransFormation.ms2SpectrumStringToNestedArray(spectrumQueryParaDto.getMs2Spectrum());
            //如需要ms2 spectrum 比對, 先不限制比數，後端比對ms2 spectrum 相似性後，再限制數據的比數
            for (int i = 0; i < spectrumDataList.size(); i++) {
                List<Double[]> refMs2Spectrum = ms2spectrumDataTransFormation.ms2SpectrumStringToNestedArray(spectrumDataList.get(i).getMs2Spectrum());
                double similarity = ms2spectrumSimilarityCalculator.calculateMS2SpectrumSimilarity(
                        expMs2Spectrum,
                        refMs2Spectrum,
                        spectrumQueryParaDto.getForwardWeight(),
                        spectrumQueryParaDto.getReverseWeight(),
                        spectrumQueryParaDto.getMs2SimilarityAlgorithm(),
                        spectrumQueryParaDto.getMs2PeakMatchTolerance()
                );
                spectrumDataList.get(i).setMs2SpectrumSimilarity(similarity);

            }

            for (int i = spectrumDataList.size() - 1; i >=0; i--) {
                if (spectrumDataList.get(i).getMs2SpectrumSimilarity() < spectrumQueryParaDto.getMs2SpectrumSimilarityTolerance()) {
                    spectrumDataList.remove(i);
                }
            }
            spectrumDataList.sort(Comparator.comparingDouble(SpectrumDataModel::getMs2SpectrumSimilarity).reversed());
        }


        return spectrumDataList;
    }

    @Override
    public List<SpectrumDataModel> getSpectrumByFuzzySearch(SpectrumQueryParaDto spectrumQueryParaDto) throws QueryParameterException {

        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ");
        sb.append("     sd.compound_data_id, ");
        sb.append("     sd.compound_classification_id, ");
        sb.append("     sd.id, ");
        sb.append("     sd.author_id, ");
        sb.append("     sd.ms_level, ");
        sb.append("     sd.precursor_mz, ");
        sb.append("     sd.exact_mass, ");
        sb.append("     sd.collision_energy, ");
        sb.append("     sd.mz_error, ");
        sb.append("     sd.last_modify, ");
        sb.append("     sd.date_created, ");
        sb.append("     sd.data_source, ");
        sb.append("     sd.tool_type, ");
        sb.append("     sd.instrument, ");
        sb.append("     sd.ion_mode, ");
        sb.append("     sd.ms2_spectrum, ");
        sb.append("     sd.precursor_type, ");
        sb.append("     cd.name, ");
        sb.append("     cd.formula, ");
        sb.append("     cd.inchi_key, ");
        sb.append("     cd.inchi, ");
        sb.append("     cd.cas, ");
        sb.append("     cd.kind, ");
        sb.append("     cd.smile ");
        sb.append(" FROM spectrum_data sd ");
        sb.append(" LEFT JOIN ms_search_library.compound_data cd ON sd.compound_data_id = cd.id ");
        sb.append(" WHERE  ");

        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(spectrumQueryParaDto.getKeyWord())) {
            throw new QueryParameterException("KeyWord is empty");
        }
        //process keyWord
        String[] keyWordArray = spectrumQueryParaDto.getKeyWord().split(" ");

        //if keyWordArray is empty, return null
        if (keyWordArray.length == 0) {
            throw new QueryParameterException("KeyWord is empty");
        }
        for (int i = 0; i < keyWordArray.length; i++) {
            //當是數字
            if (StringUtils.isEmpty(keyWordArray[i])) {
                continue;
            }
            if (keyWordArray[i].matches("-?\\d+(\\.\\d+)?")) {
                double paraDouble = Double.parseDouble(keyWordArray[i]);
                sb.append(" sd.precursor_mz >= :minPrecursorMz AND sd.precursor_mz <= :maxPrecursorMz ");
                map.put("minPrecursorMz", Double.toString(paraDouble - 0.2));
                map.put("maxPrecursorMz", Double.toString(paraDouble + 0.2));
            } else {
                //if is string
                sb.append(" cd.formula = :formula or cd.name LIKE :compoundName or cd.inchi like :inChiKey ");
                map.put("formula", keyWordArray[i]);
                map.put("compoundName", "['" + keyWordArray[i] + "%");
                map.put("inChiKey", keyWordArray[i] + "%");
            }
            sb.append(" LIMIT :spectrumInit,:spectrumOffSet ");
            map.put("spectrumInit", spectrumQueryParaDto.getSpectrumInit());
            map.put("spectrumOffSet", spectrumQueryParaDto.getSpectrumOffSet());
            break;
        }

        return namedParameterJdbcTemplate.query(sb.toString(), map, new SpectrumDataRowMapper());
    }

    @Override
    public void postSpectrum(SpectrumDataModel spectrumDataModel) throws DatabaseInsertErrorException {

        StringBuffer sb = new StringBuffer();
        sb.append(" INSERT INTO spectrum_data ");
        sb.append(" ( ");
        sb.append("     compound_data_id, ");
        sb.append("     compound_classification_id, ");
        sb.append("     author_id, ");
        sb.append("     ms_level, ");
        sb.append("     precursor_mz, ");
        sb.append("     exact_mass, ");
        sb.append("     collision_energy, ");
        sb.append("     mz_error, ");
        sb.append("     data_source, ");
        sb.append("     tool_type, ");
        sb.append("     instrument, ");
        sb.append("     ion_mode, ");
        sb.append("     ms2_spectrum, ");
        sb.append("     precursor_type ");
        sb.append(" ) ");
        sb.append(" VALUES ");
        sb.append(" ( ");
        sb.append("     :compoundDataId, ");
        sb.append("     :compoundClassificationId, ");
        sb.append("     :authorId, ");
        sb.append("     :msLevel, ");
        sb.append("     :precursorMz, ");
        sb.append("     :exactMass, ");
        sb.append("     :collisionEnergy, ");
        sb.append("     :mzError, ");
        sb.append("     :dataSource, ");
        sb.append("     :toolType, ");
        sb.append("     :instrument, ");
        sb.append("     :ionMode, ");
        sb.append("     :ms2Spectrum, ");
        sb.append("     :precursorType ");
        sb.append(" ); ");

        HashMap<String, Object> map = new HashMap<>();
        map.put("compoundDataId", spectrumDataModel.getCompoundDataId());
        map.put("compoundClassificationId", spectrumDataModel.getCompoundClassificationId());
        map.put("authorId", spectrumDataModel.getAuthorId());
        map.put("msLevel", spectrumDataModel.getMsLevel());
        map.put("precursorMz", spectrumDataModel.getPrecursorMz());
        map.put("exactMass", spectrumDataModel.getExactMass());
        map.put("collisionEnergy", spectrumDataModel.getCollisionEnergy());
        map.put("mzError", spectrumDataModel.getMzError());
        map.put("dataSource", spectrumDataModel.getDataSourceArrayList().toString());
        map.put("toolType", spectrumDataModel.getToolType());
        map.put("instrument", spectrumDataModel.getInstrument());
        map.put("ionMode", spectrumDataModel.getIonMode());
        map.put("ms2Spectrum", spectrumDataModel.getMs2Spectrum());
        map.put("precursorType", spectrumDataModel.getPrecursorType());

        int insertResult = namedParameterJdbcTemplate.update(sb.toString(), map);
        if (insertResult == 0) {
            throw new DatabaseInsertErrorException("postSpectrum failed !");
        }
    }

}