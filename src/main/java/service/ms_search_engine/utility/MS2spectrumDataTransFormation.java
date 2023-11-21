package service.ms_search_engine.utility;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MS2spectrumDataTransFormation {





    public List<Double[]> ms2SpectrumStringToNestedArray(String ms2spectrum){
        String[] peakArr = ms2spectrum.split(" ");
        int peakArrLength = peakArr.length;

        ArrayList<Double[]> peakItemArrayList = new ArrayList<>();

        for (int i = 0; i < peakArrLength; i++) {
            Double[] tempArr = new Double[2];
            String[] splitResult = peakArr[i].split(":");
            if (splitResult.length > 1){
                tempArr[0] = Double.parseDouble(splitResult[0]);
                tempArr[1] = Double.parseDouble(splitResult[1]);
            }else{
                tempArr[0] = Double.parseDouble(splitResult[0]);
                tempArr[1] = null;
            }
            peakItemArrayList.add(tempArr);
        }

        return peakItemArrayList;
    }

    public String ms2SpectrumNestedArrayToString(List<Double[]> ms2SpectrumArrayList){
        StringBuilder ms2spectrumStr = new StringBuilder();
        DecimalFormat decimalFormatMz = new DecimalFormat("0.0000000");
        DecimalFormat decimalFormatRt = new DecimalFormat("0.00");

        int arrListLength = ms2SpectrumArrayList.size();

        for (int i = 0; i < arrListLength ; i++) {
            if (ms2SpectrumArrayList.get(i).length > 1 ) {
                ms2spectrumStr.append(decimalFormatMz.format(ms2SpectrumArrayList.get(i)[0])).append(":").append(decimalFormatRt.format(ms2SpectrumArrayList.get(i)[1]));
            }
            if(ms2SpectrumArrayList.get(i).length == 1){
                ms2spectrumStr.append(decimalFormatMz.format(ms2SpectrumArrayList.get(i)[0]));
            }
            if(ms2SpectrumArrayList.get(i).length != 0){
                ms2spectrumStr.append(" ");
            }
        }
        return ms2spectrumStr.toString();
    }


    public static void main(String[] args) {
       final String testMS2spectrum = "115.0539:14.121534 139.0541:90.821997 140.0619:11.963118 150.0465:16.560166 151.0543:34.869785 152.0619:100.000000 163.0541:21.968514 165.0698:23.127242 176.0616:13.509036 179.0603:12.069145";

        MS2spectrumDataTransFormation ms2spectrumDataTransFormation = new MS2spectrumDataTransFormation();
        List<Double[]> resutArrList = ms2spectrumDataTransFormation.ms2SpectrumStringToNestedArray(testMS2spectrum);
        String resultStr = ms2spectrumDataTransFormation.ms2SpectrumNestedArrayToString(resutArrList);

        System.out.println(resutArrList);
        System.out.println(resultStr);

    }


}
