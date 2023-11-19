package service.ms_search_engine.spectrumFactory;

import lombok.Data;

import java.util.List;

@Data
public class Ms2spectrumModel {
    private int scans;
    private int msLevel;
    private String charge;
    private double retentionTime;
    private double precursorMz;
    private String peakId;
    private String featureId;
    private List<Double[]> ms2spectrumArrauList; //ex: [m/z, intensity]


    public void setExperimentDataSource(String experimentDataSource) {
    }
}
