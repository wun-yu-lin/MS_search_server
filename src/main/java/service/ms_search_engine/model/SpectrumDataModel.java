package service.ms_search_engine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.naming.Name;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SpectrumDataModel {
    //spectrum info
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
    private List<Double[]> ms2SpectrumList;
    private String precursorType;
    private Double ms2SpectrumSimilarity;
//    private String dataSource;

    //compound info
    private String formula;
    private String name;
    private String InChiKey;
    private String InChi;
    private String cas;
    private String kind;
    private String smile;


}
