package service.ms_search_engine.service;

import service.ms_search_engine.dto.CompoundQueryParaDto;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.CompoundClassificationModel;
import service.ms_search_engine.model.CompoundDataModel;

import java.util.List;

public interface CompoundService {

    //compound data
    List<CompoundDataModel> getCompoundDataByParameter(CompoundQueryParaDto compoundQueryParaDto) throws QueryParameterException;
    CompoundDataModel getCompoundDataByID(int id) throws QueryParameterException;
    void postCompoundData(CompoundDataModel compoundDataModel) throws DatabaseInsertErrorException, QueryParameterException;

    //compound classification
    List<CompoundClassificationModel> getCompoundClassificationByParameter(CompoundQueryParaDto compoundQueryParaDto) throws QueryParameterException;
    CompoundClassificationModel getCompoundClassificationByID(int id) throws QueryParameterException;
    void postCompoundClassification(CompoundClassificationModel compoundClassificationModel) throws DatabaseInsertErrorException, QueryParameterException;



}
