package service.ms_search_engine.redisService;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import service.ms_search_engine.constant.Ms2SpectrumDataSource;
import service.ms_search_engine.constant.TaskStatus;

import java.util.Date;

@Data
public class RedisSentTaskMailVO {
    private String mailAddress;
    private String subject;
    private String mainText;

    private String peakListS3FileSrc;
    private String ms2S3FileSrc;
    private String resultPeakListS3FileSrc;
    private Ms2SpectrumDataSource ms2spectrumDataSource;
    //Task submit
    private Integer taskId;
    private Double msTolerance;
    private Double msmsTolerance;
    private Double similarityTolerance;
    private Double forwardWeight;
    private Double reverseWeight;
    private String similarityAlgorithm;
    private String ionMode;
    private TaskStatus taskStatus;
    private Double ms1Ms2matchMzTolerance;
    private Double ms1Ms2matchRtTolerance;
    private Date finishTime;
    private String taskDescription;

}
