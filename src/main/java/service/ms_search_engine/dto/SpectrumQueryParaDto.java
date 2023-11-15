package service.ms_search_engine.dto;

import lombok.Data;

@Data
public class SpectrumQueryParaDto {
    private int msLevel;
    private String compoundName;
    private Double maxExactMass;
    private Double minExactMass;
    private Double maxPrecursorMz;
    private Double minPrecursorMz;
    private String precursorType;
    private String ionMode;
    private int authorId;
    private int spectrumInit;
    private int spectrumOffSet;
    private String ms2Spectrum;
    private Double ms2SpectrumSimilarityTolerance;
    private String formula;
    private String keyWord;
    private Double forwardWeight;
    private Double reverseWeight;
    private String ms2SimilarityAlgorithm;
    private Double ms2PeakMatchTolerance;

}
