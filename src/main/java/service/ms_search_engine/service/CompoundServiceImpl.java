package service.ms_search_engine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.ms_search_engine.dao.CompoundDao;
import service.ms_search_engine.dto.CompoundQueryParaDto;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.CompoundClassificationModel;
import service.ms_search_engine.model.CompoundDataModel;

import java.util.List;


@Component
public class CompoundServiceImpl implements CompoundService{

    private final CompoundDao compoundDao;

    @Autowired
    public CompoundServiceImpl(CompoundDao compoundDao) {
        this.compoundDao= compoundDao;
    }


    @Override
    public List<CompoundDataModel> getCompoundDataByParameter(CompoundQueryParaDto compoundQueryParaDto) throws QueryParameterException {
        return compoundDao.getCompoundDataByParameter(compoundQueryParaDto);
    }

    @Override
    public CompoundDataModel getCompoundDataByID(int id) throws QueryParameterException {
        return compoundDao.getCompoundDataByID(id);
    }

    @Override
    public Boolean postCompoundData(CompoundDataModel compoundDataModel) throws DatabaseInsertErrorException, QueryParameterException {
        return compoundDao.postCompoundData(compoundDataModel);
    }

    @Override
    public List<CompoundClassificationModel> getCompoundClassificationByParameter(CompoundQueryParaDto compoundQueryParaDto) throws QueryParameterException {
        return compoundDao.getCompoundClassificationByParameter(compoundQueryParaDto);
    }

    @Override
    public CompoundClassificationModel getCompoundClassificationByID(int id) throws QueryParameterException {
        return compoundDao.getCompoundClassificationByID(id);
    }

    @Override
    public Boolean postCompoundClassification(CompoundClassificationModel compoundClassificationModel) throws DatabaseInsertErrorException, QueryParameterException {
        return compoundDao.postCompoundClassification(compoundClassificationModel);
    }
}
