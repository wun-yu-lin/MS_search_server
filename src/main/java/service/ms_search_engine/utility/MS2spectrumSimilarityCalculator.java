package service.ms_search_engine.utility;

import service.ms_search_engine.exception.QueryParameterException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MS2spectrumSimilarityCalculator {
    public double calculateMS2SpectrumSimilarity(List<Double[]> expMs2Spectrum, List<Double[]>  refMs2Spectrum, double forwardWeight, double reverseWeight, String algorithm, double tolerance ) throws QueryParameterException {
        //forwardWeight and reverseWeight is the weight of forward and reverse match, mast be 0.0 to 1.0
        //tolerance is the tolerance of the m/z value for matching ms2 spectrum pair, unit is ppm
        //algorithm is the algorithm to calculate ms2 spectrum similarity, mast be intensityDeviation or dotPlot
        double similarity = 0.0;
        if (forwardWeight < 0.0 || forwardWeight > 1.0) {throw new QueryParameterException("forwardWeight mast be 0.0 to 1.0.");}
        if (reverseWeight < 0.0 || reverseWeight > 1.0) {throw new QueryParameterException("reverseWeight mast be 0.0 to 1.0.");}
        if (tolerance < 0.0) {throw new QueryParameterException("tolerance mast be greater than 0.0.");}

        //match ms2 spectrum by tolerance, add intensity to array for calculate score
        //forward
        List<Double[]> forwardMatchedSpectrum = matchSpectrumListByTolerance(expMs2Spectrum, refMs2Spectrum, "forward", tolerance); // return resize (refMs2Spectrum)
        if (forwardMatchedSpectrum.size() != expMs2Spectrum.size()){throw new RuntimeException("forwardMatchedSpectrum size not equal to expMs2Spectrum size.");}
        Double[] expItemArrForForward = new Double[expMs2Spectrum.size()];
        Double[] refItemArrForForward = new Double[forwardMatchedSpectrum.size()];
        for (int i = 0; i < expMs2Spectrum.size(); i++) {
            expItemArrForForward[i] = expMs2Spectrum.get(i)[1];
            refItemArrForForward[i] = forwardMatchedSpectrum.get(i)[1];
        }


        // reverse
        List<Double[]> reverseMatchedSpectrum = matchSpectrumListByTolerance(expMs2Spectrum, refMs2Spectrum, "reverse", tolerance); // return resize (expMs2Spectrum)
        if (reverseMatchedSpectrum.size() != refMs2Spectrum.size()){throw new RuntimeException("reverseMatchedSpectrum size not equal to refMs2Spectrum size.");}
        Double[] expItemArrForReverse=new Double[refMs2Spectrum.size()];
        Double[] refItemArrForReverse=new Double[reverseMatchedSpectrum.size()];
        for (int i = 0; i < refMs2Spectrum.size(); i++) {
            expItemArrForReverse[i] = reverseMatchedSpectrum.get(i)[1];
            refItemArrForReverse[i] = refMs2Spectrum.get(i)[1];
        }

        //calculate score
        if (Objects.equals(algorithm, "dotPlot")){
            double forwardScore = calculateScoreByDotPlot(expItemArrForForward, refItemArrForForward);
            double reverseScore = calculateScoreByDotPlot(expItemArrForReverse, refItemArrForReverse);
            similarity = forwardScore * forwardWeight + reverseScore * reverseWeight;
        }

        if (Objects.equals(algorithm, "intensityDeviation")){
            double forwardScore = calculateScoreByIntensity(expItemArrForForward, refItemArrForForward, "forward");
            double reverseScore = calculateScoreByIntensity(expItemArrForReverse, refItemArrForReverse, "reverse");
            similarity = forwardScore * forwardWeight + reverseScore * reverseWeight;

        }
        if (Double.isNaN(similarity)){similarity = 0.0;}
        return similarity;
    }
    private List<Double[]> matchSpectrumListByTolerance(List<Double[]> expMs2Spectrum, List<Double[]> refMs2Spectrum, String matchType,  double tolerance) {
        // This function is used to match two spectrum list by tolerance
        // let the expMs2Spectrum  and refMs2Spectrum match as peak pair list
        // the Peak pair algorithm is based on the hash map
        // matchType mast be forward or reverse
        // tolerance is the tolerance of the m/z value, unit is ppm



        //this value is call by reference, need noticed object using
        //if templateList exist, then use templateList to match the matchMs2SpectrumList
        //templateList exist, matchMs2SpectrumList non-exist, as pair
        //templateList not exist, not as pair
        List<Double[]> templateList = null;
        List<Double[]> matchMs2SpectrumList = null;

        if (!Objects.equals(matchType, "forward") && !Objects.equals(matchType, "reverse")) {throw new RuntimeException("matchType should be forward or reverse in matchSpectrumListByTolerance function.");}

        if(Objects.equals(matchType, "forward")) {
            templateList = expMs2Spectrum;
            matchMs2SpectrumList = refMs2Spectrum;
        }
        if(Objects.equals(matchType, "reverse")) {
            templateList = refMs2Spectrum;
            matchMs2SpectrumList = expMs2Spectrum;
        }


        //generate hash map
        HashMap<Integer, ArrayList<Double[]>> map = new HashMap<Integer, ArrayList<Double[]>>();
        for (int i = 0; i < Objects.requireNonNull(matchMs2SpectrumList).size(); i++) {
            int tempInt = (int) Math.round(matchMs2SpectrumList.get(i)[0]);
            Double[] tempList = matchMs2SpectrumList.get(i);

            if (map.containsKey(tempInt)) {
                map.get(tempInt).add(tempList);
            } else {
                ArrayList<Double[]> tempListArray = new ArrayList<Double[]>();
                tempListArray.add(tempList);
                map.put(tempInt, tempListArray);
            }
        }

        //match and generate mated peak list
        List<Double[]> generatedList = new ArrayList<Double[]>();
        for (int i = 0; i < templateList.size(); i++) {
            int tempInt = (int) Math.round(templateList.get(i)[0]);
            if (map.containsKey(tempInt)) {
                ArrayList<Double[]> tempListArray = map.get(tempInt);
                Double[] tempList = templateList.get(i);
                //if template key exist, peak match by tolerance by for loop
                int bestMatchIndex = -1;
                double bestMathDev = 1000000000.0;
                double toleranceMz = tolerance * tempList[0] / 1000000.0;
                for (int j = 0; j < tempListArray.size(); j++) {
                    if (Math.abs(tempListArray.get(j)[0] - tempList[0]) <= bestMathDev) {
                        bestMatchIndex = j;
                        bestMathDev = Math.abs(tempListArray.get(j)[0] - tempList[0]);
                    }
                }
                if (bestMatchIndex != -1 && bestMathDev < 1000000000.0 && bestMathDev <= toleranceMz) {
                    //match peak, add peak pair to generatedList
                    generatedList.add(tempListArray.get(bestMatchIndex));
                }else{
                    generatedList.add(new Double[]{templateList.get(i)[0], 0.0});
                }
            }else {
                //if template key not exist, peak not match, add peak pair with zero intensity to generatedList
                generatedList.add(new Double[]{templateList.get(i)[0], 0.0});

            }
        }






        return  generatedList;
    }

    private Double calculateScoreByDotPlot(Double[] expItemArr, Double[] refItemArr) {

        //y * z = ||y|| ||z|| cos(theta)
        double score = 0.0;
        double ySum = 0.0;
        double zSum = 0.0;
        double yzSum = 0.0;
        if (expItemArr.length != refItemArr.length) {
            throw new RuntimeException("expItemArr and refExpItemArr length not equal in calculateScoreByDotPlot function.");
        }
        for (int i = 0; i < expItemArr.length; i++) {
            yzSum = yzSum + expItemArr[i] * refItemArr[i];
            ySum = ySum + expItemArr[i] * expItemArr[i];
            zSum = zSum + refItemArr[i] * refItemArr[i];
        }

        score = yzSum / (Math.sqrt(ySum) * Math.sqrt(zSum));

        return score;
    }
    private Double calculateScoreByIntensity(Double[] expItemArr, Double[] refItemArr, String matchType) {
        double score = 0.0;
        double intSum = 0.0;
        double expRefDevSum = 0.0;

        if (expItemArr.length != refItemArr.length) {
            throw new RuntimeException("expItemArr and refExpItemArr length not equal in calculateScoreByIntensity function.");
        }
        if (!Objects.equals(matchType, "forward") && !Objects.equals(matchType, "reverse")) {
            throw new RuntimeException("matchType should be forward or reverse in calculateScoreByIntensity function.");
        }

        if (Objects.equals(matchType, "forward")) {
            for (int i = 0; i < expItemArr.length; i++) {
                intSum = intSum + expItemArr[i];
                expRefDevSum = expRefDevSum + Math.abs(expItemArr[i] - refItemArr[i]);
            }
        }

        if (Objects.equals(matchType, "reverse")) {
            for (int i = 0; i < refItemArr.length; i++) {
                intSum = intSum + refItemArr[i];
                expRefDevSum = expRefDevSum + Math.abs(expItemArr[i] - refItemArr[i]);
            }
        }

        score = 1.00 - expRefDevSum / intSum;

        return score;
    }




    //test
    public static void main(String[] args) {
        MS2spectrumSimilarityCalculator spectrumSimilarityCalculator = new MS2spectrumSimilarityCalculator();
        Double[] expItemArr = {1.0, 2.0, 3.0};
        Double[] refExpItemArr = {1.0, 2.0, 2.0};
        Double dpScore = spectrumSimilarityCalculator.calculateScoreByDotPlot(expItemArr, refExpItemArr);
        Double intensityScore = spectrumSimilarityCalculator.calculateScoreByIntensity(expItemArr, refExpItemArr, "forward");
        System.out.println(dpScore);
        System.out.println(intensityScore);



        //test matchSpectrumListByTolerance
        List<Double[]> expMs2Spectrum = new ArrayList<Double[]>();
        List<Double[]> refMs2Spectrum = new ArrayList<Double[]>();
        expMs2Spectrum.add(new Double[]{445.16, 100.0});
        expMs2Spectrum.add(new Double[]{450.16, 100.0});
        expMs2Spectrum.add(new Double[]{420.16, 100.0});
        refMs2Spectrum.add(new Double[]{445.16, 100.0});
        refMs2Spectrum.add(new Double[]{445.16, 100.0});
        refMs2Spectrum.add(new Double[]{445.16, 100.0});

        List<Double[]> generatedList = spectrumSimilarityCalculator.matchSpectrumListByTolerance(expMs2Spectrum, refMs2Spectrum, "reverse", 10.0);
        System.out.println(generatedList);
    }
}


