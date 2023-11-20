package service.ms_search_engine.batchSearchSpectrum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import service.ms_search_engine.spectrumFactory.*;

import java.util.ArrayList;

@Component
public class BatchSpectrumSearchCalculatorService {
    private BatchSearchProcessorDto batchSearchProcessorDto;

    //private String testStr = "{\"peakListFile\":null,\"peakListS3FileSrc\":\"https://d2hmlnl2t7a55p.cloudfront.net/peak_list_ff17fc37-02de-40cc-a596-1842c45cd128.csv\",\"ms2File\":null,\"ms2S3FileSrc\":\"https://d2hmlnl2t7a55p.cloudfront.net/ms2Spectrum_ff17fc37-02de-40cc-a596-1842c45cd128.mgf\",\"mail\":\"xxx@xxx.com\",\"ms2spectrumDataSource\":\"XCMS3\",\"taskId\":213,\"authorId\":0,\"msTolerance\":0.0,\"msmsTolerance\":0.0,\"similarityTolerance\":0.0,\"forwardWeight\":0.0,\"reverseWeight\":0.0,\"similarityAlgorithm\":\"dotPlot\",\"ionMode\":\"positive\",\"taskStatus\":\"SUBMIT_IN_WAITING\"}";
    private ObjectMapper mapper = new ObjectMapper();

    public BatchSpectrumSearchCalculatorService(BatchSearchProcessorDto batchSearchProcessorDto) {
        this.batchSearchProcessorDto = batchSearchProcessorDto;
    }

    public Boolean processTask() throws JsonProcessingException {
        //Write your code here
        try {
            // 1.batchSearchProcessorDto => store parameter
            // 2. ms1 peak info obj =>
            // 3. MS/MS spectrum obj in arraylist,
            // 4. ms1-ms2 peak-pair & results list obj,
            // 5. results obj for save to s3



            //read ms1 to array, ms2 data to hashmap, for ms1 & ms2 peak/spectrum matching by m/z and RT
                    //read ms1 to array
            ReadMsFile readMsPeakListFile = new ReadMsFile(batchSearchProcessorDto.getPeakListResourceUrl().getURL().getPath());
            ArrayList<Ms1peakModel> ms1PeakList = readMsPeakListFile.readMs1PeakListCsvFile();


            ReadMsFile readMs2File = new ReadMsFile(batchSearchProcessorDto.getMs2spectrumResourceUrl().getURL().getPath());
            ArrayList<Ms2spectrumModel> ms2spectrumModelList = readMs2File.readXcms3MgfFile();



            //generate ms1, ms2 peak-pair list, invoke ms1-ms2 pair generator
            PeakPairParameterModel peakPairParameterModel = new PeakPairParameterModel();
            peakPairParameterModel.setMs1Ms2matchMzTolerance(batchSearchProcessorDto.getMs1Ms2matchMzTolerance());
            peakPairParameterModel.setMs1Ms2matchRtTolerance(batchSearchProcessorDto.getMs1Ms2matchRtTolerance());
            peakPairParameterModel.setIonMode(batchSearchProcessorDto.getIonMode());
            PeakPairGenerator peakPairGenerator = new PeakPairGenerator(ms1PeakList, ms2spectrumModelList, peakPairParameterModel);
            ArrayList<PeakPairModel> ms1ms2PeakPairList =  peakPairGenerator.generatePeakPairList();


            //search ms1, ms2 peak-pair list in library, add results to ms1, ms2 peak-pair list


            //write results as file, and save to s3

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }





    public static void main(String[] args) {
        BatchSpectrumSearchCalculatorService batchSpectrumSearchCalculatorService = new BatchSpectrumSearchCalculatorService(null);
        try {
            batchSpectrumSearchCalculatorService.processTask();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}


