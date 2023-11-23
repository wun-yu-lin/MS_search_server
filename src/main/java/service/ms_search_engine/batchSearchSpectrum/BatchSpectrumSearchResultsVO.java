package service.ms_search_engine.batchSearchSpectrum;

import lombok.Data;

@Data
public class BatchSpectrumSearchResultsVO {
    //PeakPairModel
    private String expMs1FeatureId;
    private Double expPeakMz;
    private Double expPeakRt;
    private Integer expScans;
    private String expCharge;
    private Double expRetentionTime; //in seconds
    private String expMs2PeakId;
    private String expMs2FeatureId;
    private String expMs2Spectrum;

    //spectrumData model
    private Integer compoundDataId;
    private Integer compoundClassificationId;
    private Integer msLevel;
    private Double precursorMz;
    private Double exactMass;
    private String collisionEnergy;
    private String toolType;
    private String instrument;
    private String refIonMode;
    private String refMs2Spectrum;
    private String precursorType;
    private Double ms2SpectrumSimilarity;
//    private String dataSource;

    //compound info
    private String formula;
    private String name;
    private String InChiKey;
    private String InChi;
    private String cas;
    private String kind;
    private String smile;



}
