package service.ms_search_engine.batchSearchSpectrum;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import service.ms_search_engine.constant.TaskStatus;
import service.ms_search_engine.dao.BatchSearchRdbDao;
import service.ms_search_engine.dao.BatchSearchS3FileDao;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.exception.DatabaseUpdateErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.exception.RedisErrorException;
import service.ms_search_engine.redisService.RedisTaskQueueService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Component
@ConditionalOnProperty(name = "taskProcessorService.enable", matchIfMissing = false)
public class TaskProcessorService {

    private final RedisTaskQueueService redisTaskQueueService;
    private final BatchSearchRdbDao batchSearchRdbDao;
    private final BatchSearchS3FileDao batchSearchS3FileDao;

    @Autowired
    public TaskProcessorService(RedisTaskQueueService redisTaskQueueService, BatchSearchRdbDao batchSearchRdbDao, BatchSearchS3FileDao batchSearchS3FileDao) {
        this.redisTaskQueueService = redisTaskQueueService;
        this.batchSearchRdbDao = batchSearchRdbDao;
        this.batchSearchS3FileDao = batchSearchS3FileDao;
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
                        String peakListFileName =  batchSpectrumSearchDto.getPeakListS3FileSrc().split(".net/")[1];
                        batchSearchProcessorDto.setMs2spectrumResourceUrl(batchSearchS3FileDao.downloadFileByFileName(ms2FileName));
                        batchSearchProcessorDto.setPeakListResourceUrl(batchSearchS3FileDao.downloadFileByFileName(peakListFileName));
                        batchSearchProcessorDto.setMsTolerance(batchSpectrumSearchDto.getMsTolerance());
                        batchSearchProcessorDto.setMsmsTolerance(batchSpectrumSearchDto.getMsmsTolerance());
                        batchSearchProcessorDto.setSimilarityTolerance(batchSpectrumSearchDto.getSimilarityTolerance());
                        batchSearchProcessorDto.setIonMode(batchSpectrumSearchDto.getIonMode());
                        batchSearchProcessorDto.setMs1Ms2matchMzTolerance(batchSpectrumSearchDto.getMs1Ms2matchMzTolerance());
                        batchSearchProcessorDto.setMs1Ms2matchRtTolerance(batchSpectrumSearchDto.getMs1Ms2matchRtTolerance());


                        //start process
                        BatchSpectrumSearchCalculatorService batchSpectrumSearchCalculator = new BatchSpectrumSearchCalculatorService(batchSearchProcessorDto);
                        batchSpectrumSearchCalculator.processTask();


                        Thread.sleep(1000);
                        batchSpectrumSearchDto.setTaskStatus(TaskStatus.FINISH);
                        batchSearchRdbDao.updateTaskInfo(batchSpectrumSearchDto);

                        System.out.println("Processing task: " + taskDataStr);
                    } catch (Exception e) {
                        System.out.println("Error processing task: " + e.getMessage());
                        Thread.sleep(1000);
                        batchSpectrumSearchDto.setTaskStatus(TaskStatus.ERROR);
                        batchSearchRdbDao.updateTaskInfo(batchSpectrumSearchDto);

                    }finally {
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
