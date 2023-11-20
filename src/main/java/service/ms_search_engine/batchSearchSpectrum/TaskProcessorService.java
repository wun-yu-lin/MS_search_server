package service.ms_search_engine.batchSearchSpectrum;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import service.ms_search_engine.constant.TaskStatus;
import service.ms_search_engine.dao.BatchSearchRdbDao;
import service.ms_search_engine.dao.BatchSearchS3FileDao;
import service.ms_search_engine.dao.SpectrumDao;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.dto.SpectrumQueryParaDto;
import service.ms_search_engine.exception.DatabaseUpdateErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.exception.RedisErrorException;
import service.ms_search_engine.model.SpectrumDataModel;
import service.ms_search_engine.redisService.RedisTaskQueueService;
import service.ms_search_engine.spectrumFactory.PeakPairModel;
import service.ms_search_engine.utility.MS2spectrumDataTransFormation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@Component
@ConditionalOnProperty(name = "taskProcessorService.enable", matchIfMissing = false)
public class TaskProcessorService {

    private final RedisTaskQueueService redisTaskQueueService;
    private final BatchSearchRdbDao batchSearchRdbDao;
    private final BatchSearchS3FileDao batchSearchS3FileDao;

    private final SpectrumDao spectrumDao;

    @Autowired
    public TaskProcessorService(RedisTaskQueueService redisTaskQueueService, BatchSearchRdbDao batchSearchRdbDao, BatchSearchS3FileDao batchSearchS3FileDao, SpectrumDao spectrumDao) {
        this.redisTaskQueueService = redisTaskQueueService;
        this.batchSearchRdbDao = batchSearchRdbDao;
        this.batchSearchS3FileDao = batchSearchS3FileDao;
        this.spectrumDao = spectrumDao;
    }

    @PostConstruct
    @Bean
    public void listenForTasks() throws RedisErrorException, JsonProcessingException, QueryParameterException, DatabaseUpdateErrorException, InterruptedException {
        while (true) {
            try {
                if (redisTaskQueueService.queueExists()) {

                    String taskDataStr = redisTaskQueueService.getAndPopLastTask();
                    ObjectMapper mapper = new ObjectMapper();
                    BatchSpectrumSearchDto batchSpectrumSearchDto = mapper.readValue(taskDataStr, BatchSpectrumSearchDto.class);
                    BatchSearchProcessorDto batchSearchProcessorDto = new BatchSearchProcessorDto();
                    //start process task
                    try {
                        //change the task status to processing in database
                        batchSpectrumSearchDto.setTaskStatus(TaskStatus.PROCESSING);
                        batchSearchRdbDao.updateTaskInfo(batchSpectrumSearchDto);

                        Thread.sleep(1000);

                        //load & prepare parameters
                        String ms2FileName = batchSpectrumSearchDto.getMs2S3FileSrc().split(".net/")[1];
                        String peakListFileName = batchSpectrumSearchDto.getPeakListS3FileSrc().split(".net/")[1];
                        batchSearchProcessorDto.setMs2spectrumResourceUrl(batchSearchS3FileDao.downloadFileByFileName(ms2FileName));
                        batchSearchProcessorDto.setPeakListResourceUrl(batchSearchS3FileDao.downloadFileByFileName(peakListFileName));
                        batchSearchProcessorDto.setMsTolerance(batchSpectrumSearchDto.getMsTolerance());
                        batchSearchProcessorDto.setMsmsTolerance(batchSpectrumSearchDto.getMsmsTolerance());
                        batchSearchProcessorDto.setSimilarityTolerance(batchSpectrumSearchDto.getSimilarityTolerance());
                        batchSearchProcessorDto.setIonMode(batchSpectrumSearchDto.getIonMode());
                        batchSearchProcessorDto.setMs1Ms2matchMzTolerance(batchSpectrumSearchDto.getMs1Ms2matchMzTolerance());
                        batchSearchProcessorDto.setMs1Ms2matchRtTolerance(batchSpectrumSearchDto.getMs1Ms2matchRtTolerance());


                        //start process
                        BatchSpectrumSearchDataPrepare batchSpectrumSearchCalculator = new BatchSpectrumSearchDataPrepare(batchSearchProcessorDto);
                        ArrayList<PeakPairModel> peakPairModelArrayList = batchSpectrumSearchCalculator.processTaskToGenerateMsPair();
                        MS2spectrumDataTransFormation ms2spectrumDataTransFormation = new MS2spectrumDataTransFormation();
                        //search ms1, ms2 peak-pair list in library, add results to ms1, ms2 peak-pair list

                        // results list obj
                        ArrayList<BatchSpectrumSearchResultsVO> batchSpectrumSearchResultsVOArrayList = new ArrayList<>();
                        for (int i = 0; i < peakPairModelArrayList.size(); i++) {
                            SpectrumQueryParaDto tempDto = new SpectrumQueryParaDto();
                            tempDto.setMsLevel(2);
                            tempDto.setIonMode(batchSearchProcessorDto.getIonMode());
                            Double minPMz = peakPairModelArrayList.get(i).getPeakMz() - (peakPairModelArrayList.get(i).getPeakMz() * batchSearchProcessorDto.getMsTolerance() / 1000000.0);
                            Double maxPMz = peakPairModelArrayList.get(i).getPeakMz() + (peakPairModelArrayList.get(i).getPeakMz() * batchSearchProcessorDto.getMsTolerance() / 1000000.0);

                            tempDto.setMinPrecursorMz(minPMz);
                            tempDto.setMaxPrecursorMz(maxPMz);
                            if (peakPairModelArrayList.get(i).getMs2spectrumArrauList() != null) {
                                tempDto.setMs2Spectrum(ms2spectrumDataTransFormation.ms2SpectrumNestedArrayToString(peakPairModelArrayList.get(i).getMs2spectrumArrauList()));
                            }
                            tempDto.setMs2SpectrumSimilarityTolerance(batchSpectrumSearchDto.getSimilarityTolerance());
                            tempDto.setForwardWeight(batchSpectrumSearchDto.getForwardWeight());
                            tempDto.setReverseWeight(batchSpectrumSearchDto.getReverseWeight());
                            tempDto.setMs2SimilarityAlgorithm(batchSpectrumSearchDto.getSimilarityAlgorithm());
                            tempDto.setMs2PeakMatchTolerance(batchSpectrumSearchDto.getMsmsTolerance());

                            List<SpectrumDataModel> spectrumDataModelList = spectrumDao.getSpectrumByParameter(tempDto);
                            BatchSpectrumSearchResultsVO batchSpectrumSearchResultsVO = new BatchSpectrumSearchResultsVO();

                            batchSpectrumSearchResultsVO.setExpMs1FeatureId(peakPairModelArrayList.get(i).getMs1FeatureId());
                            batchSpectrumSearchResultsVO.setExpPeakMz(peakPairModelArrayList.get(i).getPeakMz());
                            batchSpectrumSearchResultsVO.setExpPeakRt(peakPairModelArrayList.get(i).getPeakRt());
                            batchSpectrumSearchResultsVO.setExpScans(peakPairModelArrayList.get(i).getScans());
                            batchSpectrumSearchResultsVO.setExpCharge(peakPairModelArrayList.get(i).getCharge());
                            batchSpectrumSearchResultsVO.setExpRetentionTime(peakPairModelArrayList.get(i).getRetentionTime());
                            batchSpectrumSearchResultsVO.setExpMs2PeakId(peakPairModelArrayList.get(i).getMs2PeakId());
                            batchSpectrumSearchResultsVO.setExpMs2FeatureId(peakPairModelArrayList.get(i).getMs2FeatureId());
                            if (peakPairModelArrayList.get(i).getMs2spectrumArrauList() != null) {
                                batchSpectrumSearchResultsVO.setExpMs2Spectrum(ms2spectrumDataTransFormation.ms2SpectrumNestedArrayToString(peakPairModelArrayList.get(i).getMs2spectrumArrauList()));
                            }
                            if (!spectrumDataModelList.isEmpty()) {
                                batchSpectrumSearchResultsVO.setCompoundDataId(spectrumDataModelList.get(0).getCompoundDataId());
                                batchSpectrumSearchResultsVO.setCompoundClassificationId(spectrumDataModelList.get(0).getCompoundClassificationId());
                                batchSpectrumSearchResultsVO.setMsLevel(spectrumDataModelList.get(0).getMsLevel());
                                batchSpectrumSearchResultsVO.setPrecursorMz(spectrumDataModelList.get(0).getPrecursorMz());
                                batchSpectrumSearchResultsVO.setExactMass(spectrumDataModelList.get(0).getExactMass());
                                batchSpectrumSearchResultsVO.setCollisionEnergy(spectrumDataModelList.get(0).getCollisionEnergy());
                                batchSpectrumSearchResultsVO.setToolType(spectrumDataModelList.get(0).getToolType());
                                batchSpectrumSearchResultsVO.setInstrument(spectrumDataModelList.get(0).getInstrument());
                                batchSpectrumSearchResultsVO.setRefIonMode(spectrumDataModelList.get(0).getIonMode());
                                batchSpectrumSearchResultsVO.setRefMs2Spectrum(spectrumDataModelList.get(0).getMs2Spectrum());
                                batchSpectrumSearchResultsVO.setMs2SpectrumSimilarity(spectrumDataModelList.get(0).getMs2SpectrumSimilarity());
                                batchSpectrumSearchResultsVO.setFormula(spectrumDataModelList.get(0).getFormula());
                                batchSpectrumSearchResultsVO.setName(spectrumDataModelList.get(0).getName());
                                batchSpectrumSearchResultsVO.setInChiKey(spectrumDataModelList.get(0).getInChiKey());
                                batchSpectrumSearchResultsVO.setInChi(spectrumDataModelList.get(0).getInChi());
                                batchSpectrumSearchResultsVO.setCas(spectrumDataModelList.get(0).getCas());
                                batchSpectrumSearchResultsVO.setKind(spectrumDataModelList.get(0).getKind());
                                batchSpectrumSearchResultsVO.setSmile(spectrumDataModelList.get(0).getSmile());
                            }

                            batchSpectrumSearchResultsVOArrayList.add(batchSpectrumSearchResultsVO);


                        }

                        //write results as file, and save to s3
                        //batchSpectrumSearchResultsVOArrayList


                        Thread.sleep(1000);
                        batchSpectrumSearchDto.setTaskStatus(TaskStatus.FINISH);
                        batchSearchRdbDao.updateTaskInfo(batchSpectrumSearchDto);

                        System.out.println("Processing task: " + taskDataStr);
                    } catch (Exception e) {
                        System.out.println("Error processing task: " + e.getMessage());
                        Thread.sleep(1000);
                        batchSpectrumSearchDto.setTaskStatus(TaskStatus.ERROR);
                        batchSearchRdbDao.updateTaskInfo(batchSpectrumSearchDto);

                    } finally {
//                        //process end, delete the file in disk
                        File ms2File = new File((batchSearchProcessorDto.getMs2spectrumResourceUrl().getURI()));
                        File peakListFile = new File((batchSearchProcessorDto.getPeakListResourceUrl().getURI()));
                        ms2File.delete();
                        peakListFile.delete();
                    }

//                processTask(taskData);
                } else {
                    // Sleep for a while if no tasks are available
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                System.out.println("Error processing task: " + e.getMessage());
            }

        }
    }


}
