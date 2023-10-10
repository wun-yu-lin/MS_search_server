package service.ms_search_engine;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class Spectrum {
    String authorName;

    @NotNull
    Float precursor_mz;
    String formula;

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Float getPrecursor_mz() {
        return precursor_mz;
    }

    public void setPrecursor_mz(Float precursor_mz) {
        this.precursor_mz = precursor_mz;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
}
