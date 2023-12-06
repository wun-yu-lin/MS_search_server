package service.ms_search_engine.service;

import org.springframework.stereotype.Component;
import service.ms_search_engine.dao.BatchSearchRdbDao;
import service.ms_search_engine.dao.MemberDao;
import service.ms_search_engine.model.BatchSpectrumSearchModel;
import service.ms_search_engine.model.MemberModel;
import service.ms_search_engine.vo.WebStatusVo;


@Component
public class WebStatusServiceImpl implements WebStatusService{

    private final MemberDao memberDao;
    private final BatchSearchRdbDao batchSearchRdbDao;

    public WebStatusServiceImpl(MemberDao memberDao, BatchSearchRdbDao batchSearchRdbDao) {
        this.memberDao = memberDao;
        this.batchSearchRdbDao = batchSearchRdbDao;
    }

    @Override
    public WebStatusVo getWebStatus() {
        MemberModel lastMember = memberDao.getLastMember();
        BatchSpectrumSearchModel batchSpectrumSearchModel = batchSearchRdbDao.getLastTask();
        WebStatusVo webStatusVo = new WebStatusVo();
        webStatusVo.setUserCount(lastMember.getId());
        webStatusVo.setTaskCount(batchSpectrumSearchModel.getId());

        return webStatusVo;
    }
}
