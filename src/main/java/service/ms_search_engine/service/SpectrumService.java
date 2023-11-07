package service.ms_search_engine.service;

import service.ms_search_engine.dto.SpectrumQueryParaDto;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.SpectrumDataModel;

import java.util.List;

public interface SpectrumService {

    public SpectrumDataModel getSpectrumByID(int id);
    public List<SpectrumDataModel> getSpectrumByParameter(SpectrumQueryParaDto spectrumQueryParaDto);
    public List<SpectrumDataModel> getSpectrumByFuzzySearch(SpectrumQueryParaDto spectrumQueryParaDto) throws QueryParameterException;
}
