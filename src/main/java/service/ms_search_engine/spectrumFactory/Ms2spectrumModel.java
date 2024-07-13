package service.ms_search_engine.spectrumFactory;

import lombok.Data;

import java.util.List;

@Data
public class Ms2spectrumModel {
    private Integer scans;
    private Integer msLevel;
    private String charge;
    private Double retentionTime;
    private Double precursorMz;
    private String peakId;
    private String featureId;
    private List<Double[]> ms2spectrumArrauList; //ex: [m/z, intensity]


    public void setExperimentDataSource(String experimentDataSource) {
    }
}
