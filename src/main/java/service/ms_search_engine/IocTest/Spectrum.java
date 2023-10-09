package service.ms_search_engine.IocTest;

import org.springframework.stereotype.Component;


public interface Spectrum {
    int spectrumNo = 0;
    void print(String spectrum_mz);
    void print_no();
}