package service.ms_search_engine.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import service.ms_search_engine.dao.MemberDao;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.DatabaseUpdateErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.MemberModel;

@Component
public class MemberServiceImpl implements MemberService{

    private final MemberDao memberDao;

    public MemberServiceImpl(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public MemberModel getMemberByPrincipalName(String principalName) throws QueryParameterException {
        return memberDao.getMemberByPrincipalName(principalName);
    }

    @Override
    @Transactional
    public Boolean postMember(MemberModel memberModel) throws QueryParameterException, DatabaseInsertErrorException {
        if (memberDao.isExistMemberByPrincipalName(memberModel.getPrincipalName())) {
            throw new DatabaseInsertErrorException("Member already exists");
        }
        if (memberModel.getPrincipalName() == null || memberModel.getPrincipalName().equals("") ||
            memberModel.getRole()==null || memberModel.getRole().equals("") ||
            memberModel.getLogInType()==null || memberModel.getLogInType().equals("")) {
            throw new QueryParameterException("parameter is null or empty");
        }

        memberDao.postMember(memberModel);

        return true;
    }

    @Override
    @Transactional
    public Boolean updateMemberLastLogInTimeByPrincipalName(String principalName) throws QueryParameterException, DatabaseUpdateErrorException, DatabaseInsertErrorException {
        if (principalName == null || principalName.equals("")) {
            throw new QueryParameterException("parameter is null or empty");
        }
        if (!memberDao.isExistMemberByPrincipalName(principalName)) {
            throw new DatabaseUpdateErrorException("Member not  exists");
        }
        memberDao.updateMemberLastLogInTimeByPrincipalName(principalName);
        return true;
    }
}
