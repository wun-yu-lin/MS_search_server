package service.ms_search_engine.dao;

import service.ms_search_engine.dto.SpectrumQueryParaDto;
import service.ms_search_engine.exception.DatabaseInsertErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.SpectrumDataModel;

import java.util.List;

public interface SpectrumDao {
    SpectrumDataModel getSpectrumByID(int id);

    List<SpectrumDataModel>  getSpectrumByParameter(SpectrumQueryParaDto spectrumQueryParaDto) throws QueryParameterException;
    List<SpectrumDataModel>  getSpectrumByFuzzySearch(SpectrumQueryParaDto spectrumQueryParaDto) throws QueryParameterException;
    void postSpectrum(SpectrumDataModel spectrumDataModel) throws DatabaseInsertErrorException;

}
