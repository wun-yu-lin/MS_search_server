package service.ms_search_engine.dao;

import org.springframework.beans.factory.annotation.Autowired;
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


    @Override
    public SpectrumDataModel getSpectrumByID(int id) {
        String sqlString = "SELECT compound_data_id, sd.compound_classification_id, sd.id, author_id, ms_level, precursor_mz, sd.exact_mass" +
                ", collision_energy, mz_error, last_modify, date_created, data_source, tool_type, instrument, ion_mode, ms2_spectrum," +
                " precursor_type, cd.name, cd.formula, cd.inchi_key, cd.inchi, cd.cas, cd.kind, cd.smile from spectrum_data sd left join ms_search_library.compound_data cd on sd.compound_data_id = cd.id WHERE sd.id = :id";
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        List<SpectrumDataModel> spectrumDataList = namedParameterJdbcTemplate.query(sqlString, map, new SpectrumDataRowMapper());
        if (spectrumDataList.isEmpty()) {
            return null;
        }
        return spectrumDataList.get(0);
    }

    @Override
    public List<SpectrumDataModel> getSpectrumByParameter(SpectrumQueryParaDto spectrumQueryParaDto) throws QueryParameterException {


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

        if (spectrumQueryParaDto.getFormula() != null) {
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
            map.put("compoundName", "['" + spectrumQueryParaDto.getCompoundName() + "%");
        }

        //如果不需要 ms2 spectrum 比對, 再限制數據的比數
        if (spectrumQueryParaDto.getMs2Spectrum() == null) {
            sqlString = sqlString + " LIMIT :spectrumInit,:spectrumOffSet";
            map.put("spectrumInit", spectrumQueryParaDto.getSpectrumInit());
            map.put("spectrumOffSet", spectrumQueryParaDto.getSpectrumOffSet());
        }else{
            if (spectrumQueryParaDto.getMaxPrecursorMz()==null || spectrumQueryParaDto.getMinPrecursorMz()==null){
                throw new QueryParameterException("MaxPrecursorMz or MinPrecursorMz can not be null when ms2 spectrum similarity is needed");
            }
        }

        List<SpectrumDataModel> spectrumDataList = namedParameterJdbcTemplate.query(sqlString, map, new SpectrumDataRowMapper());
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
                        spectrumQueryParaDto.getMs2SpectrumSimilarityTolerance()
                );
                spectrumDataList.get(i).setMs2SpectrumSimilarity(similarity);

            }

            for (int i = spectrumDataList.size() - 1; i >=0; i--) {
                if (spectrumDataList.get(i).getMs2SpectrumSimilarity() < spectrumQueryParaDto.getMs2SpectrumSimilarityTolerance()) {
                    spectrumDataList.remove(i);
                }
            }
            Collections.sort(spectrumDataList, new Comparator<SpectrumDataModel>() {
                @Override
                public int compare(SpectrumDataModel o1, SpectrumDataModel o2) {
                    return o2.getMs2SpectrumSimilarity().compareTo(o1.getMs2SpectrumSimilarity());
                }
            });
            
            
            
        }






        return spectrumDataList;
    }

    @Override
    public List<SpectrumDataModel> getSpectrumByFuzzySearch(SpectrumQueryParaDto spectrumQueryParaDto) throws QueryParameterException {

        String sqlString = "SELECT compound_data_id, sd.compound_classification_id, sd.id, author_id, ms_level, precursor_mz, sd.exact_mass" +
                ", collision_energy, mz_error, last_modify, date_created, data_source, tool_type, instrument, ion_mode, ms2_spectrum," +
                " precursor_type, cd.name, cd.formula, cd.inchi_key, cd.inchi, cd.cas, cd.kind, cd.smile from spectrum_data sd left join ms_search_library.compound_data cd on sd.compound_data_id = cd.id where ";
        Map<String, Object> map = new HashMap<>();

        if (spectrumQueryParaDto.getKeyWord() == null || spectrumQueryParaDto.getKeyWord() == "") {
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
            if (keyWordArray[i] == "") {
                continue;
            }
            if (keyWordArray[i].matches("-?\\d+(\\.\\d+)?")) {
                System.out.println("is number");
                double paraDouble = Double.parseDouble(keyWordArray[i]);
                sqlString = sqlString + " `precursor_mz` >= :minPrecursorMz AND `precursor_mz` <= :maxPrecursorMz ";
                map.put("minPrecursorMz", Double.toString(paraDouble - 0.2));
                map.put("maxPrecursorMz", Double.toString(paraDouble + 0.2));
            } else {
                //if is string
                sqlString = sqlString + "cd.formula = :formula or cd.name LIKE :compoundName or cd.inchi like :inChiKey ";
                map.put("formula", keyWordArray[i]);
                map.put("compoundName", "['" + keyWordArray[i] + "%");
                map.put("inChiKey", keyWordArray[i] + "%");
            }
            sqlString = sqlString + " LIMIT :spectrumInit,:spectrumOffSet";
            map.put("spectrumInit", spectrumQueryParaDto.getSpectrumInit());
            map.put("spectrumOffSet", spectrumQueryParaDto.getSpectrumOffSet());
            break;
        }


        List<SpectrumDataModel> spectrumDataList = namedParameterJdbcTemplate.query(sqlString, map, new SpectrumDataRowMapper());
        return spectrumDataList;
    }

    @Override
    public Boolean postSpectrum(SpectrumDataModel spectrumDataModel) throws DatabaseInsertErrorException, QueryParameterException {


        String sqlString = "INSERT INTO ms_search_library.spectrum_data (compound_data_id, compound_classification_id, author_id, ms_level, precursor_mz, exact_mass, collision_energy, mz_error, data_source, tool_type, instrument, ion_mode, ms2_spectrum, precursor_type)  " +
                " values (:compoundDataId, :compoundClassificationId, :authorId, :msLevel, " +
                "  :precursorMz, :exactMass, :collisionEnergy, :mzError, :dataSource, :toolType, "+
                " :instrument, :ionMode, :ms2Spectrum, :precursorType) ;";
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

        int insertResult = namedParameterJdbcTemplate.update(sqlString, map);
        return insertResult==1;
    }


}