package service.ms_search_engine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.ms_search_engine.dao.SpectrumDao;
import service.ms_search_engine.dto.SpectrumQueryParaDto;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.model.SpectrumDataModel;

import java.util.List;


@Component
public class SpectrumServiceImpl implements SpectrumService{

    @Autowired
    private SpectrumDao spectrumDao;

    @Override
    public SpectrumDataModel getSpectrumByID(int id) {
        return spectrumDao.getSpectrumByID(id);
    }

    @Override
    public List<SpectrumDataModel> getSpectrumByParameter(SpectrumQueryParaDto spectrumQueryParaDto) {
        return spectrumDao.getSpectrumByParameter(spectrumQueryParaDto);
    }

    @Override
    public List<SpectrumDataModel> getSpectrumByFuzzySearch(SpectrumQueryParaDto spectrumQueryParaDto) throws QueryParameterException {
        return spectrumDao.getSpectrumByFuzzySearch(spectrumQueryParaDto);
    }
}
