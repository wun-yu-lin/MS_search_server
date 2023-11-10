package service.ms_search_engine.utility;

import org.springframework.jdbc.core.RowMapper;
import service.ms_search_engine.model.CompoundDataModel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CompoundDataRowMapper implements RowMapper<CompoundDataModel> {
    @Override
    public CompoundDataModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        CompoundDataModel compoundDataModel = new CompoundDataModel();
        compoundDataModel.setId(rs.getInt("id"));
        compoundDataModel.setCompoundClassificationId(rs.getInt("compound_classification_id"));
        compoundDataModel.setName(rs.getString("name"));
        compoundDataModel.setInChiKey(rs.getString("inchi_key"));
        compoundDataModel.setInChi(rs.getString("inchi"));
        compoundDataModel.setFormula(rs.getString("formula"));
        compoundDataModel.setSmile(rs.getString("smile"));
        compoundDataModel.setCas(rs.getString("cas"));
        compoundDataModel.setExactMass(rs.getDouble("exact_mass"));
        compoundDataModel.setKind(rs.getString("kind"));
        compoundDataModel.setMoleFile(rs.getString("mole_file"));
        return compoundDataModel;
    }
}
