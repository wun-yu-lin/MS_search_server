package service.ms_search_engine.service;

import service.ms_search_engine.model.SpectrumDataModel;

import java.util.List;

public interface SpectrumService {

    public SpectrumDataModel getSpectrumByID(int id);
    public List<SpectrumDataModel> getSpectrumByParameter(int spectrumInt);
}
