package service.ms_search_engine.model;

import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.sql.Array;
import java.sql.Date;
import java.util.ArrayList;

@Component
public class SpectrumData {
    private int compoundDataId;
    private int compoundClassificationId;
    private int authorId;
    private int msLevel;
    private double precursorMz;
    private double exactMass;
    private String collisionEnergy;
    private double mzError;
    private Date lastModify;
    private Date dateCreated;
    private ArrayList<String> dataSourceArrayList;
    private String toolType;
    private String instrument;
    private String ionMode;
    private String ms2Spectrum;
    private String precursorType;

    public int getCompoundDataId() {
        return compoundDataId;
    }

    public void setCompoundDataId(int compoundDataId) {
        this.compoundDataId = compoundDataId;
    }

    public int getCompoundClassificationId() {
        return compoundClassificationId;
    }

    public void setCompoundClassificationId(int compoundClassificationId) {
        this.compoundClassificationId = compoundClassificationId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getMsLevel() {
        return msLevel;
    }

    public void setMsLevel(int msLevel) {
        this.msLevel = msLevel;
    }

    public double getPrecursorMz() {
        return precursorMz;
    }

    public void setPrecursorMz(double precursorMz) {
        this.precursorMz = precursorMz;
    }

    public double getExactMass() {
        return exactMass;
    }

    public void setExactMass(double exactMass) {
        this.exactMass = exactMass;
    }

    public String getCollisionEnergy() {
        return collisionEnergy;
    }

    public void setCollisionEnergy(String collisionEnergy) {
        this.collisionEnergy = collisionEnergy;
    }

    public double getMzError() {
        return mzError;
    }

    public void setMzError(double mzError) {
        this.mzError = mzError;
    }

    public Date getLastModify() {
        return lastModify;
    }

    public void setLastModify(Date lastModify) {
        this.lastModify = lastModify;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public ArrayList<String> getDataSourceArrayList() {
        return dataSourceArrayList;
    }

    public void setDataSourceArrayList(ArrayList<String> dataSourceArrayList) {
        this.dataSourceArrayList = dataSourceArrayList;
    }

    public String getToolType() {
        return toolType;
    }

    public void setToolType(String toolType) {
        this.toolType = toolType;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getIonMode() {
        return ionMode;
    }

    public void setIonMode(String ionMode) {
        this.ionMode = ionMode;
    }

    public String getMs2Spectrum() {
        return ms2Spectrum;
    }

    public void setMs2Spectrum(String ms2Spectrum) {
        this.ms2Spectrum = ms2Spectrum;
    }

    public String getPrecursorType() {
        return precursorType;
    }

    public void setPrecursorType(String precursorType) {
        this.precursorType = precursorType;
    }


}
