package service.ms_search_engine.dto;


import lombok.Data;

@Data
public class BatchTaskSearchDto {
   private Integer taskOffset;
   private Integer taskInit;
   private Integer authorId;
}
