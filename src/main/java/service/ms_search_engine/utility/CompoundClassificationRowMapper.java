package service.ms_search_engine.utility;

import org.springframework.jdbc.core.RowMapper;
import service.ms_search_engine.model.CompoundClassificationModel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CompoundClassificationRowMapper implements RowMapper<CompoundClassificationModel> {
    @Override
    public CompoundClassificationModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        CompoundClassificationModel compoundClassificationModel = new CompoundClassificationModel();
        compoundClassificationModel.setId(rs.getInt("id"));
        compoundClassificationModel.setClassificationKingdom(rs.getString("classification_kingdom"));
        compoundClassificationModel.setClassificationSuperclass(rs.getString("classification_superclass"));
        compoundClassificationModel.setClassificationClass(rs.getString("classification_class"));
        compoundClassificationModel.setClassificationSubclass(rs.getString("classification_subclass"));
        compoundClassificationModel.setClassificationDirectParent(rs.getString("classification_direct_parent"));


        return compoundClassificationModel;
    }
}
