package service.ms_search_engine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;

@Component
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SpectrumDataModel {
    private int id;
    private int compoundDataId;

    private int compoundClassificationId;
    private int authorId;
    private int msLevel;
    private double precursorMz;
    private double exactMass;
    private String collisionEnergy;
    private double mzError;
    private Timestamp lastModify;
    private Timestamp dateCreated;
    private ArrayList<String> dataSourceArrayList;
    private String toolType;
    private String instrument;
    private String ionMode;
    private String ms2Spectrum;
    private String precursorType;

}
