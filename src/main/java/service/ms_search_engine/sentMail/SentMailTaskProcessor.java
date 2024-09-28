package service.ms_search_engine.sentMail;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import service.ms_search_engine.constant.TaskStatus;
import service.ms_search_engine.redisService.RedisMailQueueService;
import service.ms_search_engine.redisService.RedisSentTaskMailVO;

import java.util.concurrent.FutureTask;

@Service
@Component
@ConditionalOnProperty(name = "SentMailTaskProcessorService.enable", matchIfMissing = false)
public class SentMailTaskProcessor {

    private final SentMailService sentMailService;

    private final RedisMailQueueService redisMailQueueService;

    private FutureTask<Boolean> futureTask;

    @Autowired
    public SentMailTaskProcessor(service.ms_search_engine.sentMail.SentMailService sentMailService, RedisMailQueueService redisMailQueueService) {
        this.sentMailService = sentMailService;
        this.redisMailQueueService = redisMailQueueService;
        runFutureTaskListener();
    }

    private synchronized void runFutureTaskListener(){
        if (futureTask == null){
            futureTask = new FutureTask<>(this::listenForMailTask);
            new Thread(futureTask).start();
            System.out.println("Starting SentMailTaskProcessor runFutureTaskListener...  ");
        }
    }


    private boolean listenForMailTask() {
            while (true) {
                try {
                    Thread.sleep(1000); // 避免CPU負載過高
                    if (redisMailQueueService.queueExists()) {
                        ObjectMapper mapper = new ObjectMapper();
                        String MailString = redisMailQueueService.getAndPopLastMail();
                        System.out.println("MailString: " + MailString);
                        RedisSentTaskMailVO redisSentTaskMailVO = mapper.readValue(MailString, RedisSentTaskMailVO.class);

                        if (redisSentTaskMailVO != null && redisSentTaskMailVO.getTaskStatus() == TaskStatus.SUBMIT_IN_WAITING ) {
                            sentMailService.sendTaskSubmitMail(redisSentTaskMailVO.getMailAddress(), redisSentTaskMailVO);
                        }
                        if (redisSentTaskMailVO != null && redisSentTaskMailVO.getTaskStatus() == TaskStatus.PROCESSING ) {
                            sentMailService.sendTaskInProcessMail(redisSentTaskMailVO.getMailAddress(), redisSentTaskMailVO);
                        }
                        if (redisSentTaskMailVO != null && redisSentTaskMailVO.getTaskStatus() == TaskStatus.FINISH ) {
                            sentMailService.sendTaskFinishMail(redisSentTaskMailVO.getMailAddress(), redisSentTaskMailVO);
                        }

                        if (redisSentTaskMailVO != null && redisSentTaskMailVO.getTaskStatus() == TaskStatus.ERROR ) {
                            sentMailService.sendTaskErrorMail(redisSentTaskMailVO.getMailAddress(), redisSentTaskMailVO);
                        }


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error catch: " + e.getMessage());
                } finally {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
    }

}
