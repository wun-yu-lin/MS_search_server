package service.ms_search_engine.spectrumFactory;


import lombok.Data;

@Data
public class Ms1peakModel {
    String ms1FeatureId;
    Double peakMz;
    Double peakRt;
}
