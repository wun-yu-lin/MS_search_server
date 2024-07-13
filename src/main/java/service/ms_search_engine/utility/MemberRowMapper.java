package service.ms_search_engine.utility;

import org.springframework.jdbc.core.RowMapper;
import service.ms_search_engine.model.MemberModel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberRowMapper implements RowMapper<MemberModel> {
    @Override
    public MemberModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        MemberModel memberModel = new MemberModel();
        memberModel.setId(rs.getInt("id"));
        memberModel.setPrincipalName(rs.getString("principal_name"));
        memberModel.setCreateTime(rs.getTimestamp("create_time"));
        memberModel.setLastLogInTime(rs.getTimestamp("last_log_in_time"));
        memberModel.setLogInType(rs.getString("log_in_type"));
        memberModel.setRole(rs.getString("role"));
        memberModel.setName(rs.getString("name"));
        memberModel.setEmail(rs.getString("email"));



        return memberModel;
    }
}
