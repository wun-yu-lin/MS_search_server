package service.ms_search_engine.dao;

import org.joda.time.DateTime;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.MemberModel;
import service.ms_search_engine.utility.MemberRowMapper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@Component
public class MemberDaoImpl implements MemberDao{

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public MemberDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public MemberModel getMemberByPrincipalName(String principalName) throws QueryParameterException {
        if (principalName == null) {
            throw new QueryParameterException("principalName is null");
        }
        String sqlStr = "SELECT id, principal_name, create_time, last_log_in_time, log_in_type, role, name, email FROM ms_search_library.member m WHERE m.principal_name = :principalName;";
        HashMap<String, Object> map = new HashMap<>();
        map.put("principalName", principalName);


        List<MemberModel> memberModelList = namedParameterJdbcTemplate.query(sqlStr, map, new MemberRowMapper());

        return memberModelList.size() == 0 ? null : memberModelList.get(0);
    }

    @Override
    public MemberModel getMemberById(Integer id) throws QueryParameterException {
        if(id == null) {
            throw new QueryParameterException("id is null");
        }
        String sqlStr = "SELECT id, principal_name, create_time, last_log_in_time, log_in_type, role, name, email FROM ms_search_library.member m WHERE id = :id;";
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);


        List<MemberModel> memberModelList = namedParameterJdbcTemplate.query(sqlStr, map, new MemberRowMapper());

        return memberModelList.size() == 0 ? null : memberModelList.get(0);
    }

    @Override
    public Integer postMember(MemberModel memberModel) throws DatabaseInsertErrorException, QueryParameterException {
        if (memberModel == null) {
            throw new DatabaseInsertErrorException("memberModel is null");
        }
        if (isExistMemberByPrincipalName(memberModel.getPrincipalName())) {
            throw new DatabaseInsertErrorException("member is exist");
        }

        String sqlStr = "INSERT INTO ms_search_library.member (principal_name, create_time, last_log_in_time, log_in_type, role, name, email) " +
                "VALUES (:principalName, :createTime, :lastLogInTime, :logInType, :role, :name, :email);";

        MapSqlParameterSource map = new MapSqlParameterSource();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        map.addValue("principalName", memberModel.getPrincipalName());
        map.addValue("createTime", memberModel.getCreateTime());
        map.addValue("lastLogInTime", DateTime.now().toDate());
        map.addValue("logInType", memberModel.getLogInType());
        map.addValue("role", memberModel.getRole());
        map.addValue("name", memberModel.getName());
        map.addValue("email", memberModel.getEmail());
        int insertStatus = namedParameterJdbcTemplate.update(sqlStr, map, keyHolder);
        if (insertStatus == 0) {
            throw new DatabaseInsertErrorException("insert failed");
        }
        int insertID = Objects.requireNonNull(keyHolder.getKey()).intValue();


        return insertID;
    }

    @Override
    public Boolean isExistMemberByPrincipalName(String principalName) throws QueryParameterException {
        if (principalName == null) {
            throw new QueryParameterException("principalName is null");
        }

        String sqlStr = "SELECT id, principal_name, create_time, last_log_in_time, log_in_type, role, name, email FROM ms_search_library.member m WHERE m.principal_name = :principalName;";
        HashMap<String, Object> map = new HashMap<>();
        map.put("principalName", principalName);


        List<MemberModel> memberModelList = namedParameterJdbcTemplate.query(sqlStr, map, new MemberRowMapper());

        return memberModelList.size() > 0;
    }

    @Override
    public Boolean updateMemberLastLogInTimeByPrincipalName(String principalName) throws QueryParameterException, DatabaseInsertErrorException {
        if (principalName == null) {
            throw new QueryParameterException("principalName is null");
        }
        if (!isExistMemberByPrincipalName(principalName)) {
            throw new DatabaseInsertErrorException("member is not exist");
        }
        String sqlStr = "UPDATE ms_search_library.member SET last_log_in_time = :lastLogInTime WHERE principal_name = :principalName;";
        HashMap<String, Object> map = new HashMap<>();
        map.put("principalName", principalName);
        map.put("lastLogInTime", DateTime.now().toDate());
        int updateStatus = namedParameterJdbcTemplate.update(sqlStr, map);
        if (updateStatus == 0) {
            throw new DatabaseInsertErrorException("update failed");
        }

        return true;
    }
}
