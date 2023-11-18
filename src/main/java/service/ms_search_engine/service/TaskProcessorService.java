package service.ms_search_engine.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import service.ms_search_engine.constant.TaskStatus;
import service.ms_search_engine.dao.BatchSearchRdbDao;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.exception.DatabaseUpdateErrorException;
import service.ms_search_engine.exception.QueryParameterException;
import service.ms_search_engine.exception.RedisErrorException;
import service.ms_search_engine.redisService.RedisTaskQueueService;
import service.ms_search_engine.utility.BatchSpectrumSearchCalculator;

@Service
@Component
@ConditionalOnProperty(name = "taskProcessorService.enable", matchIfMissing = false)
public class TaskProcessorService {

    private final RedisTaskQueueService redisTaskQueueService;
    private final BatchSearchRdbDao batchSearchRdbDao;

    @Autowired
    public TaskProcessorService(RedisTaskQueueService redisTaskQueueService, BatchSearchRdbDao batchSearchRdbDao) {
        this.redisTaskQueueService = redisTaskQueueService;
        this.batchSearchRdbDao = batchSearchRdbDao;
    }

    @PostConstruct
    @Bean
    public void listenForTasks() throws RedisErrorException, JsonProcessingException, QueryParameterException, DatabaseUpdateErrorException, InterruptedException {
        while (true) {
            String taskDataStr = redisTaskQueueService.getAndPopLastTask();
            if (taskDataStr != null) {
                ObjectMapper mapper = new ObjectMapper();
                BatchSpectrumSearchDto batchSpectrumSearchDto = mapper.readValue(taskDataStr, BatchSpectrumSearchDto.class);
                //start process task
                try {
                    //change the task status to processing in database
                    batchSpectrumSearchDto.setTaskStatus(TaskStatus.PROCESSING);
                    batchSearchRdbDao.updateTaskInfo(batchSpectrumSearchDto);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    BatchSpectrumSearchCalculator batchSpectrumSearchCalculator = new BatchSpectrumSearchCalculator(batchSpectrumSearchDto);
                    batchSpectrumSearchCalculator.processTask();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    batchSpectrumSearchDto.setTaskStatus(TaskStatus.FINISH);
                    batchSearchRdbDao.updateTaskInfo(batchSpectrumSearchDto);

                    System.out.println("Processing task: " + taskDataStr);
                } catch (RuntimeException e ) {
                    Thread.sleep(1000);
                    batchSpectrumSearchDto.setTaskStatus(TaskStatus.ERROR);
                    batchSearchRdbDao.updateTaskInfo(batchSpectrumSearchDto);

                    throw new RedisErrorException("Task server error");
                }

//                processTask(taskData);
            } else {
                // Sleep for a while if no tasks are available
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
