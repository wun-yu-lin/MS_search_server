package service.ms_search_engine.batchSearchSpectrum;

import lombok.Data;
import org.springframework.core.io.UrlResource;

@Data
public class BatchSearchProcessorDto {
    private UrlResource ms2spectrumResourceUrl;
    private UrlResource peakListResourceUrl;
    private String searchResultUrl;
    private Double msTolerance;
    private Double msmsTolerance;
    private Double similarityTolerance;
    private Double forwardWeight;
    private Double reverseWeight;
    private String similarityAlgorithm;
    private String ionMode;
    private Double ms1Ms2matchMzTolerance;
    private Double ms1Ms2matchRtTolerance;

}
