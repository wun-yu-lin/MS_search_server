package service.ms_search_engine.IocTest;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class SpectrumImp implements Spectrum{

    private int spectrumNo;


    @Override
    public void print(String spectrum_mz) {
        System.out.println(spectrum_mz);
    }

    @PostConstruct //初始化的時候執行
    public void initSpectrumNo(){
        spectrumNo = 100;
    }


    @Override
    public void print_no() {
        spectrumNo--;
        System.out.println("spectrumNo: "+ Integer.toString(spectrumNo));

    }


}
