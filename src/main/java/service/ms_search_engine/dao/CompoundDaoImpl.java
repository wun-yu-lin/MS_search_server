package service.ms_search_engine.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import service.ms_search_engine.dto.CompoundQueryParaDto;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.CompoundClassificationModel;
import service.ms_search_engine.model.CompoundDataModel;
import service.ms_search_engine.utility.CompoundClassificationRowMapper;
import service.ms_search_engine.utility.CompoundDataRowMapper;

import java.util.HashMap;
import java.util.List;

@Component
public class CompoundDaoImpl implements CompoundDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public CompoundDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }


    @Override
    public List<CompoundDataModel> getCompoundDataByParameter(CompoundQueryParaDto compoundQueryParaDto) throws QueryParameterException {

        String sqlString = "SELECT cd.compound_classification_id, cd.id, cd.name, cd.inchi_key, cd.inchi, cd.formula, cd.smile, cd.cas, cd.exact_mass, cd.mole_file, cd.kind FROM ms_search_library.compound_data cd WHERE 1=1";
        HashMap<String, Object> map = new HashMap<>();
        if (compoundQueryParaDto.getInChiKey() != null) {
            sqlString = sqlString + " AND cd.inchi_key = :inChiKey";
            map.put("inChiKey", compoundQueryParaDto.getInChiKey());
        }

        List<CompoundDataModel> compoundDataModelList = namedParameterJdbcTemplate.query(sqlString, map, new CompoundDataRowMapper());
        return compoundDataModelList;
    }

    @Override
    public CompoundDataModel getCompoundDataByID(int id) throws QueryParameterException {
        if (id < 0) {
            throw new QueryParameterException("SQL query failed, id must be positive integer");
        }

        String sqlString = "SELECT cd.compound_classification_id, cd.id, cd.name, cd.inchi_key, cd.inchi, cd.formula, cd.smile, cd.cas, cd.exact_mass, cd.mole_file, cd.kind FROM ms_search_library.compound_data cd WHERE id = :id;";
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        CompoundDataModel compoundDataModel = namedParameterJdbcTemplate.queryForObject(sqlString, map, new CompoundDataRowMapper());
        return compoundDataModel;
    }

    @Override
    public Boolean postCompoundData(CompoundDataModel compoundDataModel) throws DatabaseInsertErrorException, QueryParameterException {
        //check if the compound data with the same InChiKey already exists in the database, throw error
        CompoundQueryParaDto compoundQueryParaDto = new CompoundQueryParaDto();
        compoundQueryParaDto.setInChiKey(compoundDataModel.getInChiKey());
        List<CompoundDataModel> compoundDataModelList = this.getCompoundDataByParameter(compoundQueryParaDto);
        if(compoundDataModelList.size()!= 0){
            throw new DatabaseInsertErrorException("The compound data with the same InChiKey already exists in the database");
        }

        String sqlString = "INSERT INTO ms_search_library.compound_data (compound_classification_id, name, inchi_key, inchi, formula, smile, cas, exact_mass, mole_file, kind) VALUES" +
                "(:compoundClassificationId, :name, :inChiKey, :inChi, :formula, :smile, :cas, :exactMass, :moleFile, :kind)";
        HashMap<String, Object> map = new HashMap<>();
        map.put("compoundClassificationId", compoundDataModel.getCompoundClassificationId());
        map.put("name", compoundDataModel.getName());
        map.put("inChiKey", compoundDataModel.getInChiKey());
        map.put("inChi", compoundDataModel.getInChi());
        map.put("formula", compoundDataModel.getFormula());
        map.put("smile", compoundDataModel.getSmile());
        map.put("cas", compoundDataModel.getCas());
        map.put("exactMass", compoundDataModel.getExactMass());
        map.put("moleFile", compoundDataModel.getMoleFile());
        map.put("kind", compoundDataModel.getKind());

        int insertStatus = namedParameterJdbcTemplate.update(sqlString, map);

        return insertStatus == 1;
    }

    @Override
    public List<CompoundClassificationModel> getCompoundClassificationByParameter(CompoundQueryParaDto compoundQueryParaDto) throws QueryParameterException {
        String sqlString = "SELECT cc.id, cc.classification_kingdom, cc.classification_superclass, cc.classification_class, cc.classification_subclass, cc.classification_direct_parent FROM ms_search_library.compound_classification cc WHERE 1=1 ";
        HashMap<String, Object> map = new HashMap<>();
        if (compoundQueryParaDto.getClassificationDirectParent() == null) {
            throw new QueryParameterException("SQL query failed, classificationDirectParent must not be null");
        }
        if (compoundQueryParaDto.getClassificationDirectParent() != null) {
            sqlString = sqlString + " AND cc.classification_direct_parent = :classificationDirectParent";
            map.put("classificationDirectParent", compoundQueryParaDto.getClassificationDirectParent());
        }

        List<CompoundClassificationModel> compoundClassificationModelList = namedParameterJdbcTemplate.query(sqlString, map, new CompoundClassificationRowMapper());

        return compoundClassificationModelList;
    }

    @Override
    public CompoundClassificationModel getCompoundClassificationByID(int id) throws QueryParameterException {
        if (id < 0) {
            throw new QueryParameterException("SQL query failed, id must be positive integer");
        }
        String sqlString = "SELECT cc.id, cc.classification_kingdom, cc.classification_superclass, cc.classification_class, cc.classification_subclass, cc.classification_direct_parent FROM ms_search_library.compound_classification cc WHERE cc.id = :id;";
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        CompoundClassificationModel compoundClassificationModel = namedParameterJdbcTemplate.queryForObject(sqlString, map, new CompoundClassificationRowMapper());

        return compoundClassificationModel;
    }

    @Override
    public Boolean postCompoundClassification(CompoundClassificationModel compoundClassificationModel) throws DatabaseInsertErrorException, QueryParameterException {
        //check if the compound classification table with the same data already exists in the database, throw error
        CompoundQueryParaDto compoundQueryParaDto = new CompoundQueryParaDto();
        compoundQueryParaDto.setClassificationDirectParent(compoundClassificationModel.getClassificationDirectParent());
        List<CompoundClassificationModel> compoundClassificationModelList = this.getCompoundClassificationByParameter(compoundQueryParaDto);
        if(compoundClassificationModelList.size()!= 0){
            throw new DatabaseInsertErrorException("The compound data with the same InChiKey already exists in the database");
        }
        String sqlString = "INSERT INTO ms_search_library.compound_classification "+
                " (classification_kingdom, classification_superclass, classification_class, " +
                " classification_subclass, classification_direct_parent) VALUES " +
                " (:classificationKingdom, :classificationSuperclass, :classificationClass, " +
                " :classificationSubclass, :classificationDirectParent);";

        HashMap<String, Object> map = new HashMap<>();
        map.put("classificationKingdom", compoundClassificationModel.getClassificationKingdom());
        map.put("classificationSuperclass", compoundClassificationModel.getClassificationSuperclass());
        map.put("classificationClass", compoundClassificationModel.getClassificationClass());
        map.put("classificationSubclass", compoundClassificationModel.getClassificationSubclass());
        map.put("classificationDirectParent", compoundClassificationModel.getClassificationDirectParent());

        int insertStatus = namedParameterJdbcTemplate.update(sqlString, map);
        return insertStatus == 1;
    }
}
