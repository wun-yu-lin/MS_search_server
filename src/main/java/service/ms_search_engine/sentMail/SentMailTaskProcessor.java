package service.ms_search_engine.sentMail;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import service.ms_search_engine.constant.TaskStatus;
import service.ms_search_engine.redisService.RedisMailQueueService;
import service.ms_search_engine.redisService.RedisSentTaskMailVO;

@Service
@Component
@ConditionalOnProperty(name = "SentMailTaskProcessorService.enable", matchIfMissing = false)
public class SentMailTaskProcessor {

    private final SentMailService sentMailService;
    private final RedisMailQueueService redisMailQueueService;

    @Autowired
    public SentMailTaskProcessor(service.ms_search_engine.sentMail.SentMailService sentMailService, RedisMailQueueService redisMailQueueService) {
        this.sentMailService = sentMailService;
        this.redisMailQueueService = redisMailQueueService;
    }

    @PostConstruct
    @Bean
    public void listenForMailTask() {
            while (true) {
                try {
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
