package service.ms_search_engine.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import service.ms_search_engine.constant.Ms2SpectrumDataSource;

@Data
public class BatchSpectrumSearchDto {
    //File upload
    private MultipartFile peakListFile;
    private MultipartFile ms2File;
    private String mail;
    private Ms2SpectrumDataSource ms2spectrumDataSource;
    //Task submit
    private String taskId;
    private String authorId;
    private Double msTolerance;
    private Double msmsTolerance;
    private Double similarityTolerance;
    private Double forwardWeight;
    private Double reverseWeight;
    private String similarityAlgorithm;
    private String ionMode;

    public void getMs2spectrumDataSource(Ms2SpectrumDataSource ms2spectrumDataSource) {
        this.ms2spectrumDataSource = ms2spectrumDataSource;
    }
}

