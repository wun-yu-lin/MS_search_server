package service.ms_search_engine.spectrumFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class PeakPairGenerator {
    private final ArrayList<Ms1peakModel> ms1PeakList;
    private final ArrayList<Ms2spectrumModel> ms2spectrumModelList;

    private ArrayList<PeakPairModel> peakPairModelArrayList;

    private final PeakPairParameterModel peakPairParameterModel;


    public PeakPairGenerator(ArrayList<Ms1peakModel> ms1PeakList, ArrayList<Ms2spectrumModel> ms2spectrumModelList, PeakPairParameterModel peakPairParameterModel) {
        this.ms1PeakList = ms1PeakList;
        this.ms2spectrumModelList = ms2spectrumModelList;
        this.peakPairParameterModel = peakPairParameterModel;
        this.peakPairModelArrayList = new ArrayList<PeakPairModel>();
    }

    public ArrayList<PeakPairModel> generatePeakPairList(){

        //parameter    this.peakPairParameterModel
        //results store   this.peakPairModelArrayList;


        //ms2 data to hashmap
        HashMap<String, ArrayList<Ms2spectrumModel>> ms2spectrumModelHashMap = new HashMap<>();
        for (int i = 0; i < this.ms2spectrumModelList.size(); i++) {
            int tempPreMzInt = (int) Math.round(this.ms2spectrumModelList.get(i).getPrecursorMz());
            int tempRtInt = (int) Math.round(this.ms2spectrumModelList.get(i).getRetentionTime()/60);
            String tempKey = (String) (tempPreMzInt + "_" + tempRtInt);
            if(ms2spectrumModelHashMap.containsKey(tempKey)) {
                ms2spectrumModelHashMap.get(tempKey).add(this.ms2spectrumModelList.get(i));
            }else {
                ArrayList<Ms2spectrumModel> tempArrayList = new ArrayList<>();
                tempArrayList.add(this.ms2spectrumModelList.get(i));
                ms2spectrumModelHashMap.put(tempKey, tempArrayList);
            }

        }

        //ms1 peakMatch with ms2 spectrumHashMap
        for (int i = 0; i < this.ms1PeakList.size(); i++) {

            //cuurent ms1 peak key to search in ms2 spectrumHashMap
            int tempPreMzInt = (int) Math.round(this.ms1PeakList.get(i).getPeakMz());
            int tempRtInt = (int) Math.round(this.ms1PeakList.get(i).getPeakRt()/60);
            String tempStr = (String) (tempPreMzInt + "_" + tempRtInt);
            PeakPairModel tempPeakPairModel = new PeakPairModel();
            Ms1peakModel tempMs1Model =  this.ms1PeakList.get(i);

            if (ms2spectrumModelHashMap.containsKey(tempStr)){
                ArrayList<Ms2spectrumModel> tempArrayList = ms2spectrumModelHashMap.get(tempStr);

                int bestMatchIndex = -1;
                double bestMatchMzDev = 1000000000.0;
                double bestMatchRtDev = 1000000000.0;
                double toleranceMz = this.peakPairParameterModel.getMs1Ms2matchMzTolerance() * tempMs1Model.getPeakMz() / 1000000.0;
                double toleranceRt = this.peakPairParameterModel.getMs1Ms2matchRtTolerance();

                for (int i1 = 0; i1 < tempArrayList.size(); i1++) {
                    if (Math.abs(tempArrayList.get(i1).getPrecursorMz() - tempMs1Model.getPeakMz()) <= bestMatchMzDev){
                        if (Math.abs(tempArrayList.get(i1).getRetentionTime() - tempMs1Model.getPeakRt()) <= bestMatchRtDev){
                            bestMatchIndex = i1;
                            bestMatchMzDev = Math.abs(tempArrayList.get(i1).getPrecursorMz() - tempMs1Model.getPeakMz());
                            bestMatchRtDev = Math.abs(tempArrayList.get(i1).getRetentionTime() - tempMs1Model.getPeakRt());
                        }

                    }
                }

                if (bestMatchIndex !=-1 && bestMatchMzDev < toleranceMz && bestMatchRtDev < toleranceRt){
                    //match peak, add peak pair to generatedList

                    Ms2spectrumModel tempMs2Spectrum =  tempArrayList.get(bestMatchIndex);
                    //ms1
                    tempPeakPairModel.setMs1FeatureId(tempMs1Model.getMs1FeatureId());
                    tempPeakPairModel.setPeakMz(tempMs1Model.getPeakMz());
                    tempPeakPairModel.setPeakRt(tempMs1Model.getPeakRt());
                    //ms2
                    tempPeakPairModel.setScans(tempMs2Spectrum.getScans());
                    tempPeakPairModel.setCharge(tempMs2Spectrum.getCharge());
                    tempPeakPairModel.setRetentionTime(tempMs2Spectrum.getRetentionTime());
                    tempPeakPairModel.setPrecursorMz(tempMs2Spectrum.getPrecursorMz());
                    tempPeakPairModel.setMs2PeakId(tempMs2Spectrum.getPeakId());
                    tempPeakPairModel.setMs2FeatureId(tempMs2Spectrum.getFeatureId());
                    tempPeakPairModel.setMs2spectrumArrauList(tempMs2Spectrum.getMs2spectrumArrauList());

                }else{
                    //ms1 only, no ms2 match
                    tempPeakPairModel.setMs1FeatureId(tempMs1Model.getMs1FeatureId());
                    tempPeakPairModel.setPeakMz(tempMs1Model.getPeakMz());
                    tempPeakPairModel.setPeakRt(tempMs1Model.getPeakRt());

                }
                this.peakPairModelArrayList.add(tempPeakPairModel);

            }else {
                //ms1
                tempPeakPairModel.setMs1FeatureId(tempMs1Model.getMs1FeatureId());
                tempPeakPairModel.setPeakMz(tempMs1Model.getPeakMz());
                tempPeakPairModel.setPeakRt(tempMs1Model.getPeakRt());
                this.peakPairModelArrayList.add(tempPeakPairModel);
            }


        }

        return this.peakPairModelArrayList;
    }
}
