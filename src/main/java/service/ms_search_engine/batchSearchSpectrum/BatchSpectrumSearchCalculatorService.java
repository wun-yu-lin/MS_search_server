package service.ms_search_engine.batchSearchSpectrum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.spectrumFactory.Ms2spectrumModel;
import service.ms_search_engine.spectrumFactory.ReadMsFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;


public class BatchSpectrumSearchCalculatorService {
    private BatchSpectrumSearchDto batchSpectrumSearchDto;

    private String testStr = "{\"peakListFile\":null,\"peakListS3FileSrc\":\"https://d2hmlnl2t7a55p.cloudfront.net/peak_list_ff17fc37-02de-40cc-a596-1842c45cd128.csv\",\"ms2File\":null,\"ms2S3FileSrc\":\"https://d2hmlnl2t7a55p.cloudfront.net/ms2Spectrum_ff17fc37-02de-40cc-a596-1842c45cd128.mgf\",\"mail\":\"xxx@xxx.com\",\"ms2spectrumDataSource\":\"XCMS3\",\"taskId\":213,\"authorId\":0,\"msTolerance\":0.0,\"msmsTolerance\":0.0,\"similarityTolerance\":0.0,\"forwardWeight\":0.0,\"reverseWeight\":0.0,\"similarityAlgorithm\":\"dotPlot\",\"ionMode\":\"positive\",\"taskStatus\":\"SUBMIT_IN_WAITING\"}";
    private ObjectMapper mapper = new ObjectMapper();

    public BatchSpectrumSearchCalculatorService(BatchSpectrumSearchDto batchSpectrumSearchDto) {
        this.batchSpectrumSearchDto = batchSpectrumSearchDto;
    }

    public Boolean processTask() throws JsonProcessingException {
        //Write your code here
        //testing data
        this.batchSpectrumSearchDto = mapper.readValue(testStr, BatchSpectrumSearchDto.class);
        try {
            //object 1.batchSpectrumSearchDto, 2. ms1 peak info obj, 3. MS/MS spectrum obj, 4. ms1-ms2 peak-pair & results list obj, 5. results obj for save to s3

            //download s3 data in stream


            //read ms1 to array, ms2 data to hashmap, for ms1 & ms2 peak/spectrum matching by m/z and RT
                    //read ms1 to array



            ReadMsFile readMsFile = new ReadMsFile("/Users/linwunyu/Documents/ms rawdata uplaod test/xcms/s/ms2spectra_all_DDA_binsize0.02_obp.mgf");
            ArrayList<Ms2spectrumModel> ms2spectrumModelList = readMsFile.readXcms3MgfFile();

                //ms2 data to hashmap
            HashMap<String, ArrayList<Ms2spectrumModel>> ms2spectrumModelHashMap = new HashMap<>();
            for (int i = 0; i < ms2spectrumModelList.size(); i++) {
                int tempPreMzInt = (int) Math.round(ms2spectrumModelList.get(i).getPrecursorMz());
                int tempRtInt = (int) Math.round(ms2spectrumModelList.get(i).getRetentionTime()/60);
                String tempKey = (String) (tempPreMzInt + "_" + tempRtInt);
//                Double tempDouble = BigDecimal.valueOf(ms2spectrumModelList.get(i).getPrecursorMz()).setScale(2, RoundingMode.HALF_UP).doubleValue();
                if(ms2spectrumModelHashMap.containsKey(tempKey)) {
                    ms2spectrumModelHashMap.get(tempKey).add(ms2spectrumModelList.get(i));
                }else {
                    ArrayList<Ms2spectrumModel> tempArrayList = new ArrayList<>();
                    tempArrayList.add(ms2spectrumModelList.get(i));
                    ms2spectrumModelHashMap.put(tempKey, tempArrayList);
                }

            }

            //generate ms1, ms2 peak-pair list


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


