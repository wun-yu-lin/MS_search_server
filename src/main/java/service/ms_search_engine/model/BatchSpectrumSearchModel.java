package service.ms_search_engine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.stereotype.Component;

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






}
