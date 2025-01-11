package service.ms_search_engine.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchSearchReqJson extends BaseRequestData {
    private Integer id;
    private Integer authorId;
    private Integer taskStatus;
    private String s3PeakListSrc;
    private String s3Ms2FileSrc;
    private String s3ResultsSrc;
    private Double msTolerance;
    private Double msmsTolerance;
    private Double similarityTolerance;
    private Double forwardWeight;
    private Double reverseWeight;
    private String similarityAlgorithm;
    private String ionMode;
    private String mail;
    private String ms2spectrumDataSource;
    private Date createTime;
    private Date finishTime;
    private Double ms1Ms2matchMzTolerance;
    private Double ms1Ms2matchRtTolerance;
    private String taskDescription;
}
