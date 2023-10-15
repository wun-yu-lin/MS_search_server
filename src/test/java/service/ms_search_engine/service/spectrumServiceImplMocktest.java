package service.ms_search_engine.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import service.ms_search_engine.dao.SpectrumDao;
import service.ms_search_engine.model.SpectrumDataModel;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class spectrumServiceImplMocktest {

    @Autowired
    SpectrumService spectrumService;

    @MockBean
    private SpectrumDao spectrumDao;


    @BeforeEach
    public void beforeEach(){
        SpectrumDataModel mockSpectrumDataModel = new SpectrumDataModel();
        mockSpectrumDataModel.setAuthorId(1);
        Mockito.when(spectrumDao.getSpectrumByID(Mockito.anyInt())).thenReturn(mockSpectrumDataModel);
    }


    @Test
    public void getSpectrumByID() {
        SpectrumDataModel spectrumDataModel = spectrumDao.getSpectrumByID(1);
        assertEquals(1, spectrumDataModel.getAuthorId());
    }
    @Test
    public void getSpectrumByID2() {
        SpectrumDataModel spectrumDataModel = spectrumDao.getSpectrumByID(1);
        assertEquals(1, spectrumDataModel.getAuthorId());
    }

}