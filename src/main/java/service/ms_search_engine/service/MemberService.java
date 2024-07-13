package service.ms_search_engine.service;

import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.DatabaseUpdateErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.MemberModel;

public interface MemberService {
    MemberModel getMemberByPrincipalName(String principalName) throws QueryParameterException;
    Boolean postMember(MemberModel memberModel) throws QueryParameterException, DatabaseInsertErrorException;
    Boolean updateMemberLastLogInTimeByPrincipalName(String principalName) throws QueryParameterException, DatabaseUpdateErrorException, DatabaseInsertErrorException;

}
