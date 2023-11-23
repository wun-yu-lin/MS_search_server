package service.ms_search_engine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BatchSpectrumSearchModel {
   private int id;
   private int authorId;
   private int taskStatus;
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
