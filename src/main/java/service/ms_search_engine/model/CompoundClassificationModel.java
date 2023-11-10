package service.ms_search_engine.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class CompoundClassificationModel {
   private int id;
   private String classificationKingdom;
   private String classificationSuperclass;
   private String classificationClass;
   private String classificationSubclass;
   private String classificationDirectParent;
}
