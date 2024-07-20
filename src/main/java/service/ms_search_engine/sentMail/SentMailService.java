package service.ms_search_engine.sentMail;

import jakarta.mail.MessagingException;
import service.ms_search_engine.exception.S3DataDownloadException;
import service.ms_search_engine.redisService.RedisSentTaskMailVO;

import java.io.IOException;


public interface SentMailService {

    boolean sendMailWithText(String address, String subject, String text) throws MessagingException;
    boolean sendTaskSubmitMail(String address, RedisSentTaskMailVO redisSentTaskMailVO) throws MessagingException;
    boolean sendTaskInProcessMail(String address, RedisSentTaskMailVO redisSentTaskMailVO) throws MessagingException;
    boolean sendTaskFinishMail(String address, RedisSentTaskMailVO redisSentTaskMailVO) throws MessagingException, S3DataDownloadException, IOException;
    boolean sendTaskErrorMail(String address, RedisSentTaskMailVO redisSentTaskMailVO) throws MessagingException;





}
