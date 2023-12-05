package service.ms_search_engine.dao;

import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.MemberModel;

public interface MemberDao {
    MemberModel getMemberByPrincipalName(String principalName) throws QueryParameterException;
    MemberModel getMemberById(Integer id) throws QueryParameterException;
    Integer postMember(MemberModel memberModel) throws DatabaseInsertErrorException, QueryParameterException;
    Boolean isExistMemberByPrincipalName(String principalName) throws QueryParameterException;
    Boolean updateMemberLastLogInTimeByPrincipalName(String principalName) throws QueryParameterException, DatabaseInsertErrorException;
}
