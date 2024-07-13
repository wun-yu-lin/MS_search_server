package service.ms_search_engine.spectrumFactory;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@NotNull
@Data
public class PeakPairParameterModel {
    private String ionMode;
    private Double ms1Ms2matchMzTolerance;
    private Double ms1Ms2matchRtTolerance;
}
