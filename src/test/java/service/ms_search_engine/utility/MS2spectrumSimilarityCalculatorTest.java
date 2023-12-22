package service.ms_search_engine.utility;

import org.junit.jupiter.api.Test;
import service.ms_search_engine.exception.QueryParameterException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MS2spectrumSimilarityCalculatorTest {

    @Test
    public void test1CalculateMS2SpectrumSimilarity() throws QueryParameterException {
        MS2spectrumSimilarityCalculator ms2spectrumSimilarityCalculator = new MS2spectrumSimilarityCalculator();
        List<Double[]> expMs2Spectrum = new ArrayList<>();
        expMs2Spectrum.add(new Double[]{1.0, 1.0});
        expMs2Spectrum.add(new Double[]{2.0, 2.0});
        expMs2Spectrum.add(new Double[]{3.0, 3.0});
        expMs2Spectrum.add(new Double[]{4.0, 4.0});
        expMs2Spectrum.add(new Double[]{5.0, 5.0});
        List<Double[]> refMs2Spectrum= new ArrayList<>();
        refMs2Spectrum.add(new Double[]{1.0, 1.0});
        refMs2Spectrum.add(new Double[]{2.0, 2.0});
        refMs2Spectrum.add(new Double[]{3.0, 3.0});
        refMs2Spectrum.add(new Double[]{4.0, 4.0});
        refMs2Spectrum.add(new Double[]{5.0, 5.0});

        double result1 = ms2spectrumSimilarityCalculator.calculateMS2SpectrumSimilarity(expMs2Spectrum, refMs2Spectrum, 0.0, 1.0, "dotPlot", 20);
        double result2 = ms2spectrumSimilarityCalculator.calculateMS2SpectrumSimilarity(expMs2Spectrum, refMs2Spectrum, 1, 0.0, "dotPlot", 20);
        assertEquals(1, result1);
        assertEquals(1, result2);
    }

    @Test
    public void test2CalculateMS2SpectrumSimilarity() throws QueryParameterException {
        MS2spectrumSimilarityCalculator ms2spectrumSimilarityCalculator = new MS2spectrumSimilarityCalculator();
        List<Double[]> expMs2Spectrum = new ArrayList<>();
        expMs2Spectrum.add(new Double[]{100.0, 0.0});
        expMs2Spectrum.add(new Double[]{187.0766, 10.0});
        expMs2Spectrum.add(new Double[]{201.0923, 3.76});
        expMs2Spectrum.add(new Double[]{445.16, 100.0});
        List<Double[]> refMs2Spectrum= new ArrayList<>();
        refMs2Spectrum.add(new Double[]{100.0, 0.0});
        refMs2Spectrum.add(new Double[]{187.0766, 8.0});
        refMs2Spectrum.add(new Double[]{201.0930, 20.0});
        refMs2Spectrum.add(new Double[]{445.162, 90.0});

        double result1 = ms2spectrumSimilarityCalculator.calculateMS2SpectrumSimilarity(expMs2Spectrum, refMs2Spectrum, 0.0, 1.0, "dotPlot", 20);
        double result2 = ms2spectrumSimilarityCalculator.calculateMS2SpectrumSimilarity(expMs2Spectrum, refMs2Spectrum, 1, 0.0, "dotPlot", 20);
        System.out.println(result1);
        System.out.println(result2);
        assertEquals(0.9837054509316032, result1);
        assertEquals(0.9837054509316032, result2);
    }




}