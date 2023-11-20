package service.ms_search_engine.spectrumFactory;

import lombok.Data;

import java.util.List;

@Data
public class PeakPairModel {
    private String ms1FeatureId;
    private Double peakMz;
    private Double peakRt;
    private int scans;
    private String charge;
    private double retentionTime; //in seconds
    private double precursorMz; //in mass to charge ratio
    private String ms2PeakId;
    private String ms2FeatureId;
    private List<Double[]> ms2spectrumArrauList; //ex: [m/z, intensity]

}
