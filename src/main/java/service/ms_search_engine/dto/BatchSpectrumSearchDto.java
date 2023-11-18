package service.ms_search_engine.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import service.ms_search_engine.constant.Ms2SpectrumDataSource;
import service.ms_search_engine.constant.TaskStatus;

@Data
public class BatchSpectrumSearchDto {
    //File upload
    private MultipartFile peakListFile;
    private String peakListS3FileSrc;
    private MultipartFile ms2File;
    private String ms2S3FileSrc;
    private String mail;
    private Ms2SpectrumDataSource ms2spectrumDataSource;
    //Task submit
    private Integer taskId;
    private Integer authorId;
    private Double msTolerance;
    private Double msmsTolerance;
    private Double similarityTolerance;
    private Double forwardWeight;
    private Double reverseWeight;
    private String similarityAlgorithm;
    private String ionMode;
    private TaskStatus taskStatus;


    public void getMs2spectrumDataSource(Ms2SpectrumDataSource ms2spectrumDataSource) {
        this.ms2spectrumDataSource = ms2spectrumDataSource;
    }
}

