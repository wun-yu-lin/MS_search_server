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
import java.util.Map;

@Component
public class CompoundDaoImpl implements CompoundDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public CompoundDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }


    @Override
    public List<CompoundDataModel> getCompoundDataByParameter(CompoundQueryParaDto compoundQueryParaDto) throws QueryParameterException {

        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(" SELECT ");
        sqlBuffer.append("     cd.compound_classification_id, ");
        sqlBuffer.append("     cd.id, ");
        sqlBuffer.append("     cd.name, ");
        sqlBuffer.append("     cd.inchi_key, ");
        sqlBuffer.append("     cd.inchi, ");
        sqlBuffer.append("     cd.formula, ");
        sqlBuffer.append("     cd.smile, ");
        sqlBuffer.append("     cd.cas, ");
        sqlBuffer.append("     cd.exact_mass, ");
        sqlBuffer.append("     cd.mole_file, ");
        sqlBuffer.append("     cd.kind ");
        sqlBuffer.append(" FROM ms_search_library.compound_data cd ");
        sqlBuffer.append(" WHERE 1=1 ");

        Map<String, Object> map = new HashMap<>();
        if (compoundQueryParaDto.getInChiKey() != null) {
            sqlBuffer.append(" AND cd.inchi_key = :inChiKey ");
            map.put("inChiKey", compoundQueryParaDto.getInChiKey());
        }

        return namedParameterJdbcTemplate.query(sqlBuffer.toString(), map, new CompoundDataRowMapper());
    }

    @Override
    public CompoundDataModel getCompoundDataByID(int id) throws QueryParameterException {
        if (id < 0) {
            throw new QueryParameterException("SQL query failed, id must be positive integer");
        }
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(" SELECT ");
        sqlBuffer.append("     cd.compound_classification_id, ");
        sqlBuffer.append("     cd.id, ");
        sqlBuffer.append("     cd.name, ");
        sqlBuffer.append("     cd.inchi_key, ");
        sqlBuffer.append("     cd.inchi, ");
        sqlBuffer.append("     cd.formula, ");
        sqlBuffer.append("     cd.smile, ");
        sqlBuffer.append("     cd.cas, ");
        sqlBuffer.append("     cd.exact_mass, ");
        sqlBuffer.append("     cd.mole_file, ");
        sqlBuffer.append("     cd.kind ");
        sqlBuffer.append(" FROM ms_search_library.compound_data cd ");
        sqlBuffer.append(" WHERE cd.id = :id;");

        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return namedParameterJdbcTemplate.queryForObject(sqlBuffer.toString(), map, new CompoundDataRowMapper());
    }

    @Override
    public void postCompoundData(CompoundDataModel compoundDataModel) throws DatabaseInsertErrorException, QueryParameterException {
        //check if the compound data with the same InChiKey already exists in the database, throw error
        CompoundQueryParaDto compoundQueryParaDto = new CompoundQueryParaDto();
        compoundQueryParaDto.setInChiKey(compoundDataModel.getInChiKey());
        List<CompoundDataModel> compoundDataModelList = this.getCompoundDataByParameter(compoundQueryParaDto);
        if(compoundDataModelList.size()!= 0){
            throw new DatabaseInsertErrorException("The compound data with the same InChiKey already exists in the database");
        }

        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(" INSERT INTO ms_search_library.compound_data ");
        sqlBuffer.append(" ( ");
        sqlBuffer.append("     compound_classification_id, ");
        sqlBuffer.append("     name, ");
        sqlBuffer.append("     inchi_key, ");
        sqlBuffer.append("     inchi, ");
        sqlBuffer.append("     formula, ");
        sqlBuffer.append("     smile, ");
        sqlBuffer.append("     cas, ");
        sqlBuffer.append("     exact_mass, ");
        sqlBuffer.append("     mole_file, ");
        sqlBuffer.append("     kind ");
        sqlBuffer.append(" ) ");
        sqlBuffer.append(" VALUES ");
        sqlBuffer.append(" ( ");
        sqlBuffer.append("     :compoundClassificationId, ");
        sqlBuffer.append("     :name, ");
        sqlBuffer.append("     :inChiKey, ");
        sqlBuffer.append("     :inChi, ");
        sqlBuffer.append("     :formula, ");
        sqlBuffer.append("     :smile, ");
        sqlBuffer.append("     :cas, ");
        sqlBuffer.append("     :exactMass, ");
        sqlBuffer.append("     :moleFile, ");
        sqlBuffer.append("     :kind ");
        sqlBuffer.append(" ); ");
        Map<String, Object> map = new HashMap<>();
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

        int insertStatus = namedParameterJdbcTemplate.update(sqlBuffer.toString(), map);
        if (insertStatus == 0) {
            throw new DatabaseInsertErrorException("postCompoundData error!");
        }
    }

    @Override
    public List<CompoundClassificationModel> getCompoundClassificationByParameter(CompoundQueryParaDto compoundQueryParaDto) throws QueryParameterException {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(" SELECT ");
        sqlBuffer.append("     cc.id, ");
        sqlBuffer.append("     cc.classification_kingdom, ");
        sqlBuffer.append("     cc.classification_superclass, ");
        sqlBuffer.append("     cc.classification_class, ");
        sqlBuffer.append("     cc.classification_subclass, ");
        sqlBuffer.append("     cc.classification_direct_parent ");
        sqlBuffer.append(" FROM ms_search_library.compound_classification cc ");
        sqlBuffer.append(" WHERE 1=1 ");
        Map<String, Object> map = new HashMap<>();
        if (compoundQueryParaDto.getClassificationDirectParent() == null) {
            throw new QueryParameterException("SQL query failed, classificationDirectParent must not be null");
        }
        sqlBuffer.append(" AND cc.classification_direct_parent = :classificationDirectParent ");
        map.put("classificationDirectParent", compoundQueryParaDto.getClassificationDirectParent());

        return namedParameterJdbcTemplate.query(sqlBuffer.toString(), map, new CompoundClassificationRowMapper());
    }

    @Override
    public CompoundClassificationModel getCompoundClassificationByID(int id) throws QueryParameterException {
        if (id < 0) {
            throw new QueryParameterException("SQL query failed, id must be positive integer");
        }
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT ");
        sqlBuffer.append("    cc.id, ");
        sqlBuffer.append("    cc.classification_kingdom, ");
        sqlBuffer.append("    cc.classification_superclass, ");
        sqlBuffer.append("    cc.classification_class, ");
        sqlBuffer.append("    cc.classification_subclass, ");
        sqlBuffer.append("    cc.classification_direct_parent ");
        sqlBuffer.append("FROM ms_search_library.compound_classification cc ");
        sqlBuffer.append("WHERE cc.id = :id ");

        Map<String, Object> map = new HashMap<>();
        map.put("id", id);

        return namedParameterJdbcTemplate.queryForObject(sqlBuffer.toString(), map, new CompoundClassificationRowMapper());
    }

    @Override
    public void postCompoundClassification(CompoundClassificationModel compoundClassificationModel) throws DatabaseInsertErrorException, QueryParameterException {
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

        Map<String, Object> map = new HashMap<>();
        map.put("classificationKingdom", compoundClassificationModel.getClassificationKingdom());
        map.put("classificationSuperclass", compoundClassificationModel.getClassificationSuperclass());
        map.put("classificationClass", compoundClassificationModel.getClassificationClass());
        map.put("classificationSubclass", compoundClassificationModel.getClassificationSubclass());
        map.put("classificationDirectParent", compoundClassificationModel.getClassificationDirectParent());

        int insertStatus = namedParameterJdbcTemplate.update(sqlString, map);
        if (insertStatus == 0){
            throw new DatabaseInsertErrorException("postCompoundClassification error!");
        }
    }
}
