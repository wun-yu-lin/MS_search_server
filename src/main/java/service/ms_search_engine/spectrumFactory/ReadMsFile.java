package service.ms_search_engine.spectrumFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ReadMsFile {
    private final String filePath;

    public ReadMsFile(String filePath) {
        this.filePath = filePath;
//        this.filePath = "/Users/linwunyu/Documents/ms rawdata uplaod test/xcms/s/ms2spectra_all_DDA_binsize0.02_obp.mgf";
    }

    public ArrayList<Ms2spectrumModel> readXcms3MgfFile() throws FileNotFoundException {
        ArrayList<Ms2spectrumModel> ms2spectrumModelList = new ArrayList<>();


        //TODO
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = null;
            Ms2spectrumModel ms2spectrumModel = null;
            Boolean startRead = false;


            while ((line = reader.readLine()) != null) {
                //if line is BEGIN IONS, create new Ms2spectrumModel obj
                if (Objects.equals(line, "BEGIN IONS")) {
                    startRead = true;
                    ms2spectrumModel = new Ms2spectrumModel();
                    ms2spectrumModel.setMs2spectrumArrauList(new ArrayList<>());

                    continue;
                }
                //read spectrum info fields
                if (Objects.equals(line.split("=")[0], "SCANS")) {
                    assert ms2spectrumModel != null;
                    ms2spectrumModel.setScans(Integer.parseInt(line.split("=")[1]));

                } else if (Objects.equals(line.split("=")[0], "TITLE")) {
                    assert ms2spectrumModel != null;
                    ms2spectrumModel.setMsLevel(Integer.parseInt(line.split("=")[1].split(" ")[1].replace(";", "")));

                } else if (Objects.equals(line.split("=")[0], "RTINSECONDS")) {
                    assert ms2spectrumModel != null;
                    ms2spectrumModel.setRetentionTime(Double.parseDouble(line.split("=")[1]));

                } else if (Objects.equals(line.split("=")[0], "PEPMASS")) {
                    assert ms2spectrumModel != null;
                    ms2spectrumModel.setPrecursorMz(Double.parseDouble(line.split("=")[1]));

                } else if (Objects.equals(line.split("=")[0], "FEATURE_ID")) {
                    assert ms2spectrumModel != null;
                    ms2spectrumModel.setFeatureId(line.split("=")[1]);

                } else if (Objects.equals(line.split("=")[0], "PEAK_ID")) {
                    assert ms2spectrumModel != null;
                    ms2spectrumModel.setPeakId(line.split("=")[1]);

                } else if (Objects.equals(line.split("=")[0], "CHARGE")) {
                    assert ms2spectrumModel != null;
                    ms2spectrumModel.setCharge(line.split("=")[1]);

                } else if (!Objects.equals(line.split("=")[0], "END IONS") && startRead) {
                    //save m/z and intensity to list, intensity is not normalized,  current is absolute value
                    ms2spectrumModel.getMs2spectrumArrauList().add(new Double[]{Double.parseDouble(line.split(" ")[0]), Double.parseDouble(line.split(" ")[1])});

                }

                //if line is END IONS, add Ms2spectrumModel obj to list, and normalize intensity of ms2 spectrum
                if (Objects.equals(line, "END IONS")) {
                    assert ms2spectrumModel != null;
                    //normalize intensity of ms2 spectrum to %, max is 100%
                    double intensityMax = 0;
                    for (int i = 0; i < ms2spectrumModel.getMs2spectrumArrauList().size(); i++) {
                        if (ms2spectrumModel.getMs2spectrumArrauList().get(i)[1] > intensityMax) {
                            intensityMax = ms2spectrumModel.getMs2spectrumArrauList().get(i)[1];
                        }
                    }
                    for (int i = 0; i < ms2spectrumModel.getMs2spectrumArrauList().size(); i++) {
                        ms2spectrumModel.getMs2spectrumArrauList().get(i)[1] = (ms2spectrumModel.getMs2spectrumArrauList().get(i)[1] / intensityMax)*100;
                    }

                    ms2spectrumModelList.add(ms2spectrumModel);
                    startRead = false;
                }

            }
        } catch (IOException ignored) {

        }

        return ms2spectrumModelList;
    }

    public static void main(String[] args) {
        ReadMsFile readMsFile = new ReadMsFile("");
        try {
            ArrayList<Ms2spectrumModel> ms2spectrumModelArrayList = readMsFile.readXcms3MgfFile();
            System.out.println(ms2spectrumModelArrayList.size());
            System.out.println("done");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
