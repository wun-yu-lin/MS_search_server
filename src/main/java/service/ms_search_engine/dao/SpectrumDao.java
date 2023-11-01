package service.ms_search_engine.dao;

import service.ms_search_engine.dto.SpectrumQueryParaDto;
import service.ms_search_engine.model.SpectrumDataModel;

import java.util.List;

public interface SpectrumDao {
    public SpectrumDataModel getSpectrumByID(int id);

    public List<SpectrumDataModel>  getSpectrumByParameter(SpectrumQueryParaDto spectrumQueryParaDto);

}
