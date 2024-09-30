package service.ms_search_engine.batchSearchSpectrum;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import service.ms_search_engine.config.ServerConfig;
import service.ms_search_engine.constant.TaskStatus;
import service.ms_search_engine.dao.BatchSearchRdbDao;
import service.ms_search_engine.dao.BatchSearchS3FileDao;
import service.ms_search_engine.dao.SpectrumDao;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.dto.SpectrumQueryParaDto;
import service.ms_search_engine.exception.DatabaseUpdateErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.exception.RedisErrorException;
import service.ms_search_engine.model.BatchSpectrumSearchModel;
import service.ms_search_engine.model.SpectrumDataModel;
import service.ms_search_engine.redisService.RedisMailQueueService;
import service.ms_search_engine.redisService.RedisSentTaskMailVO;
import service.ms_search_engine.redisService.RedisTaskQueueService;
import service.ms_search_engine.spectrumFactory.PeakPairModel;
import service.ms_search_engine.utility.MS2spectrumDataTransFormation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.FutureTask;

@Service
@Component
@ConditionalOnProperty(name = "taskProcessorService.enable", matchIfMissing = false)
public class TaskProcessorService {

    private final RedisTaskQueueService redisTaskQueueService;

    private final RedisMailQueueService redisMailQueueService;

    private final BatchSearchRdbDao batchSearchRdbDao;

    private final BatchSearchS3FileDao batchSearchS3FileDao;

    private final SpectrumDao spectrumDao;


    @Autowired
    private ServerConfig config;

    private FutureTask<Boolean> futureTask;

    @Autowired
    public TaskProcessorService(RedisTaskQueueService redisTaskQueueService, RedisMailQueueService redisMailQueueService, BatchSearchRdbDao batchSearchRdbDao, BatchSearchS3FileDao batchSearchS3FileDao, SpectrumDao spectrumDao) {
        this.redisTaskQueueService = redisTaskQueueService;
        this.redisMailQueueService = redisMailQueueService;
        this.batchSearchRdbDao = batchSearchRdbDao;
        this.batchSearchS3FileDao = batchSearchS3FileDao;
        this.spectrumDao = spectrumDao;
        runFutureTaskListener();
    }

    private synchronized void runFutureTaskListener(){
        if (this.futureTask == null){
            this.futureTask = new FutureTask<>(this::listenForTasks);
            new Thread(this.futureTask).start();
            System.out.println("starting TaskProcessorService runFutureTaskListener...");
        }

    }


    private boolean listenForTasks() throws RedisErrorException, JsonProcessingException, QueryParameterException, DatabaseUpdateErrorException, InterruptedException {

        while (true) {
            try {
                if (redisTaskQueueService.queueExists()) {
                    String awsCloudFrontEndpoint = config.getAwsCloudFrontEndpoint();
                    String taskDataStr = redisTaskQueueService.getAndPopLastTask();
                    ObjectMapper mapper = new ObjectMapper();
                    BatchSpectrumSearchDto batchSpectrumSearchDto = mapper.readValue(taskDataStr, BatchSpectrumSearchDto.class);
                    BatchSearchProcessorDto batchSearchProcessorDto = new BatchSearchProcessorDto();
                    RedisSentTaskMailVO redisSentTaskMailVO = new RedisSentTaskMailVO();
                    //start process task
                    try {
                        //check task status in database, if not in pending, return error
                        BatchSpectrumSearchModel batchSpectrumSearchModel = batchSearchRdbDao.getTaskInfoById(batchSpectrumSearchDto.getTaskId());
                        if (batchSpectrumSearchModel == null) {
                            continue;
                        }
                        if (batchSpectrumSearchModel.getTaskStatus() != TaskStatus.SUBMIT_IN_WAITING.getStatusCode()) {
                            System.out.println("Task is not in waiting status, possible reason: task is processing, finished, deleted or error");
                            continue;
                        }


                        //change the task status to processing in database
                        batchSpectrumSearchDto.setTaskStatus(TaskStatus.PROCESSING);
                        batchSearchRdbDao.updateTaskInfo(batchSpectrumSearchDto);

                        Thread.sleep(1000);

                        //load & prepare parameters
                        ////load parameter for processing
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

                        //load parameter for sending email
                        redisSentTaskMailVO.setMailAddress(batchSpectrumSearchDto.getMail());
                        redisSentTaskMailVO.setSubject("Task " + batchSpectrumSearchDto.getTaskId() + " in processing");
                        redisSentTaskMailVO.setMainText("Task " + batchSpectrumSearchDto.getTaskId() + " is in processing, please wait for the result");
                        redisSentTaskMailVO.setPeakListS3FileSrc(batchSpectrumSearchDto.getPeakListS3FileSrc());
                        redisSentTaskMailVO.setMs2S3FileSrc(batchSpectrumSearchDto.getMs2S3FileSrc());
                        redisSentTaskMailVO.setMs2spectrumDataSource(batchSpectrumSearchDto.getMs2spectrumDataSource());
                        redisSentTaskMailVO.setTaskId(batchSpectrumSearchDto.getTaskId());
                        redisSentTaskMailVO.setMsTolerance(batchSpectrumSearchDto.getMsTolerance());
                        redisSentTaskMailVO.setMsmsTolerance(batchSpectrumSearchDto.getMsmsTolerance());
                        redisSentTaskMailVO.setSimilarityTolerance(batchSpectrumSearchDto.getSimilarityTolerance());
                        redisSentTaskMailVO.setForwardWeight(batchSpectrumSearchDto.getForwardWeight());
                        redisSentTaskMailVO.setReverseWeight(batchSpectrumSearchDto.getReverseWeight());
                        redisSentTaskMailVO.setSimilarityAlgorithm(batchSpectrumSearchDto.getSimilarityAlgorithm());
                        redisSentTaskMailVO.setIonMode(batchSpectrumSearchDto.getIonMode());
                        redisSentTaskMailVO.setTaskStatus(TaskStatus.PROCESSING);
                        redisSentTaskMailVO.setMs1Ms2matchMzTolerance(batchSpectrumSearchDto.getMs1Ms2matchMzTolerance());
                        redisSentTaskMailVO.setMs1Ms2matchRtTolerance(batchSpectrumSearchDto.getMs1Ms2matchRtTolerance());
                        redisSentTaskMailVO.setTaskDescription(batchSpectrumSearchDto.getTaskDescription());
                        String mailString = mapper.writeValueAsString(redisSentTaskMailVO);



                        //check download exist or not, if not exist,download again and wait for 10s, with 3 times retry

                        //ms2spectrum *3
                        if (batchSearchProcessorDto.getMs2spectrumResourceUrl() == null) {
                            Thread.sleep(10000);
                            batchSearchProcessorDto.setMs2spectrumResourceUrl(batchSearchS3FileDao.downloadFileByFileName(ms2FileName));
                        }
                        if (batchSearchProcessorDto.getMs2spectrumResourceUrl() == null) {
                            Thread.sleep(10000);
                            batchSearchProcessorDto.setMs2spectrumResourceUrl(batchSearchS3FileDao.downloadFileByFileName(ms2FileName));
                        }
                        if (batchSearchProcessorDto.getMs2spectrumResourceUrl() == null) {
                            Thread.sleep(10000);
                            batchSearchProcessorDto.setMs2spectrumResourceUrl(batchSearchS3FileDao.downloadFileByFileName(ms2FileName));
                        }

                        //Peak List * 3
                        if (batchSearchProcessorDto.getPeakListResourceUrl() == null) {
                            Thread.sleep(10000);
                            batchSearchProcessorDto.setMs2spectrumResourceUrl(batchSearchS3FileDao.downloadFileByFileName(ms2FileName));
                        }
                        if (batchSearchProcessorDto.getPeakListResourceUrl() == null) {
                            Thread.sleep(10000);
                            batchSearchProcessorDto.setMs2spectrumResourceUrl(batchSearchS3FileDao.downloadFileByFileName(ms2FileName));
                        }
                        if (batchSearchProcessorDto.getPeakListResourceUrl() == null) {
                            Thread.sleep(10000);
                            batchSearchProcessorDto.setMs2spectrumResourceUrl(batchSearchS3FileDao.downloadFileByFileName(ms2FileName));
                        }


                        //job start and send email
//                        mailString = mapper.writeValueAsString(redisSentTaskMailVO);
//                        redisMailQueueService.newMail (mailString);

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
                            if (batchSpectrumSearchDto.getIonMode() == null) {
                                batchSpectrumSearchResultsVO.setExpCharge("All");
                            } else if (batchSpectrumSearchDto.getIonMode().equals("negative")) {
                                batchSpectrumSearchResultsVO.setExpCharge("Neg");
                            } else if (batchSpectrumSearchDto.getIonMode().equals("positive")){
                                batchSpectrumSearchResultsVO.setExpCharge("Pos");
                            }else {
                                throw new QueryParameterException("Ion mode error");
                            }
                            //batchSpectrumSearchResultsVO.setExpCharge(batchSpectrumSearchDto.getIonMode());
                            batchSpectrumSearchResultsVO.setExpRetentionTime(peakPairModelArrayList.get(i).getRetentionTime());
                            batchSpectrumSearchResultsVO.setExpMs2PeakId(peakPairModelArrayList.get(i).getMs2PeakId());
                            batchSpectrumSearchResultsVO.setExpMs2FeatureId(peakPairModelArrayList.get(i).getMs2FeatureId());
                            if (peakPairModelArrayList.get(i).getMs2spectrumArrauList() != null) {
                                if (peakPairModelArrayList.get(i).getMs2spectrumArrauList().size() > 1000) {
                                    batchSpectrumSearchResultsVO.setExpMs2Spectrum("Y, but too many peaks, please search in your raw data");
                                } else {
                                    batchSpectrumSearchResultsVO.setExpMs2Spectrum(ms2spectrumDataTransFormation.ms2SpectrumNestedArrayToString(peakPairModelArrayList.get(i).getMs2spectrumArrauList()));
                                }
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
                                if (spectrumDataModelList.get(0).getMs2Spectrum() != null) {
                                    if (ms2spectrumDataTransFormation.ms2SpectrumStringToNestedArray(spectrumDataModelList.get(0).getMs2Spectrum()).size() > 1000) {
                                        batchSpectrumSearchResultsVO.setRefMs2Spectrum("Y, but too many peaks, please search in library");
                                    } else {
                                        batchSpectrumSearchResultsVO.setRefMs2Spectrum(spectrumDataModelList.get(0).getMs2Spectrum());
                                    }
                                } else {
                                    batchSpectrumSearchResultsVO.setRefMs2Spectrum("N/A");
                                }
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
                        //prepare results to cvs
                        List<String[]> resultList = new ArrayList<String[]>();
                        String[] header = new String[28];
                        header[0] = "M_feature_id";
                        header[1] = "mz";
                        header[2] = "rt";
                        header[3] = "scans";
                        header[4] = "para_ion_mode";
                        header[5] = "exp_spectrum_retention_time";
                        header[6] = "exp_ms2_peak_id";
                        header[7] = "exp_ms2_feature_id";
                        header[8] = "exp_ms2_spectrum";
                        header[9] = "ref_compound_data_id";
                        header[10] = "ref_compound_classification_id";
                        header[11] = "ref_ms_level";
                        header[12] = "ref_precursor_mz";
                        header[13] = "ref_exact_mass";
                        header[14] = "ref_collision_energy";
                        header[15] = "ref_tool_type";
                        header[16] = "ref_instrument";
                        header[17] = "ref_ion_mode";
                        header[18] = "ref_ms2_spectrum";
                        header[19] = "ref_precursor_type";
                        header[20] = "ms2_spectrum_similarity";
                        header[21] = "ref_formula";
                        header[22] = "ref_compound_name";
                        header[23] = "ref_inchi_key";
                        header[24] = "ref_inchi";
                        header[25] = "ref_cas";
                        header[26] = "ref_kind";
                        header[27] = "ref_smile";
                        resultList.add(header); //append header to result list

                        HashMap<String, Integer> columnMapper = new HashMap<>();
                        columnMapper.put("expMs1FeatureId", 0);
                        columnMapper.put("expPeakMz", 1);
                        columnMapper.put("expPeakRt", 2);
                        columnMapper.put("expScans", 3);
                        columnMapper.put("expCharge", 4);
                        columnMapper.put("expRetentionTime", 5);
                        columnMapper.put("expMs2PeakId", 6);
                        columnMapper.put("expMs2FeatureId", 7);
                        columnMapper.put("expMs2Spectrum", 8);
                        columnMapper.put("compoundDataId", 9);
                        columnMapper.put("compoundClassificationId", 10);
                        columnMapper.put("msLevel", 11);
                        columnMapper.put("precursorMz", 12);
                        columnMapper.put("exactMass", 13);
                        columnMapper.put("collisionEnergy", 14);
                        columnMapper.put("toolType", 15);
                        columnMapper.put("instrument", 16);
                        columnMapper.put("refIonMode", 17);
                        columnMapper.put("refMs2Spectrum", 18);
                        columnMapper.put("precursorType", 19);
                        columnMapper.put("ms2SpectrumSimilarity", 20);
                        columnMapper.put("formula", 21);
                        columnMapper.put("name", 22);
                        columnMapper.put("inChiKey", 23);
                        columnMapper.put("inChi", 24);
                        columnMapper.put("cas", 25);
                        columnMapper.put("kind", 26);
                        columnMapper.put("smile", 27);


                        for (int i = 0; i < batchSpectrumSearchResultsVOArrayList.size(); i++) {
                            String[] tempArr = new String[28];
                            BatchSpectrumSearchResultsVO tempVo = batchSpectrumSearchResultsVOArrayList.get(i);
                            Class<?> clazz = tempVo.getClass();
                            Field[] fs = clazz.getDeclaredFields();

                            for (Field f : fs) {
                                f.setAccessible(true);
                                String cName = f.getName();
                                int mappedIndex = columnMapper.get(cName);
                                if (f.get(tempVo) == null) {
                                    tempArr[mappedIndex] = "";
                                } else if (f.get(tempVo) instanceof String) {
                                    tempArr[mappedIndex] = (String) f.get(tempVo);
                                } else if (f.get(tempVo) instanceof Double) {
                                    tempArr[mappedIndex] = String.valueOf(f.get(tempVo));
                                } else {
                                    tempArr[mappedIndex] = f.get(tempVo).toString();
                                }
                            }
                            resultList.add(tempArr);
                        }


                        //write results as file, and save to s3
                        //batchSpectrumSearchResultsVOArrayList
                        File f = new File("results.csv");
                        try (ICSVWriter writer = new CSVWriter(new FileWriter("./results.csv"))) {
                            writer.writeAll(resultList);
                        } catch (Exception e) {
                            throw new DatabaseUpdateErrorException("Error writing results to file: " + e.getMessage());
                        }
                        try {
                            f = new File("./results.csv");
                            FileInputStream input = new FileInputStream(f);
                            MultipartFile multipartFile = new MockMultipartFile(f.getName(), input);
                            //save to s3
                            batchSpectrumSearchDto.setResultPeakListFile(multipartFile);
                            batchSpectrumSearchDto = batchSearchS3FileDao.postFileUpload(batchSpectrumSearchDto);
                            if (batchSpectrumSearchDto.getResultPeakListS3FileSrc() == null) {
                                //if upload failed, delete the record in s3 and return error
                                throw new DatabaseUpdateErrorException("S3 data upload failed");
                            }

                        } catch (Exception e) {
                            throw new DatabaseUpdateErrorException("Error writing results to file: " + e.getMessage());

                        } finally {
                            boolean isFileDelete = f.delete();
                        }


                        Thread.sleep(1000);
                        //update task status to finish in database and send email
                        batchSpectrumSearchDto.setTaskStatus(TaskStatus.FINISH);
                        batchSpectrumSearchDto.setFinishTime(DateTime.now().toDate());
                        batchSearchRdbDao.updateTaskInfo(batchSpectrumSearchDto);
                        redisSentTaskMailVO.setSubject("Task " + batchSpectrumSearchDto.getTaskId() + " is finished");
                        redisSentTaskMailVO.setMainText("Task " + batchSpectrumSearchDto.getTaskId() + " is finished, please check the result");
                        redisSentTaskMailVO.setTaskStatus(TaskStatus.FINISH);
                        redisSentTaskMailVO.setResultPeakListS3FileSrc(awsCloudFrontEndpoint + batchSpectrumSearchDto.getResultPeakListS3FileSrc());
                        redisSentTaskMailVO.setFinishTime(batchSpectrumSearchDto.getFinishTime());
                        mailString = mapper.writeValueAsString(redisSentTaskMailVO);
                        redisMailQueueService.newMail (mailString);


                        System.out.println("Processing task: " + taskDataStr);
                    } catch (Exception e) {
                        System.out.println("Error processing task: " + e.getMessage());
                        e.printStackTrace();
                        Thread.sleep(1000);
                        //update task status to error in database and send email
                        batchSpectrumSearchDto.setTaskStatus(TaskStatus.ERROR);
                        batchSpectrumSearchDto.setFinishTime(DateTime.now().toDate());
                        batchSearchRdbDao.updateTaskInfo(batchSpectrumSearchDto);
                        redisSentTaskMailVO.setSubject("Task " + batchSpectrumSearchDto.getTaskId() + " is Failed");
                        redisSentTaskMailVO.setMainText("Task " + batchSpectrumSearchDto.getTaskId() + " is Failed, please check the file & parameters, resubmit the task");
                        redisSentTaskMailVO.setTaskStatus(TaskStatus.ERROR);
                        String mailString = mapper.writeValueAsString(redisSentTaskMailVO);
                        redisMailQueueService.newMail (mailString);

                    } finally {
//                        //process end, delete the file in disk
                        File ms2File = new File((batchSearchProcessorDto.getMs2spectrumResourceUrl().getURI()));
                        File peakListFile = new File((batchSearchProcessorDto.getPeakListResourceUrl().getURI()));
                        boolean isDeleteMs2File = ms2File.delete();
                        boolean isDeletePeakListFile = peakListFile.delete();
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
                e.printStackTrace();
            }

        }
    }


}
