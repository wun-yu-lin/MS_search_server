package service.ms_search_engine.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class CompoundDataModel {
    private int id;
    private int compoundClassificationId;
    private String name;
    private String inChiKey;
    private String inChi;
    private String formula;
    private String smile;
    private String cas;
    private Double exactMass;
    private String kind;
    private String moleFile;

}
