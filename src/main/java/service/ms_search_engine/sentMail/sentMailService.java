package service.ms_search_engine.sentMail;

import jakarta.mail.MessagingException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.exception.S3DataDownloadException;
import service.ms_search_engine.redisService.RedisSentTaskMailVO;

import java.io.IOException;


public interface sentMailService {

    boolean sendMailWithText(String address, String subject, String text) throws MessagingException;
    boolean sendTaskSubmitMail(String address, RedisSentTaskMailVO redisSentTaskMailVO) throws MessagingException;
    boolean sendTaskInProcessMail(String address, RedisSentTaskMailVO redisSentTaskMailVO) throws MessagingException;
    boolean sendTaskFinishMail(String address, RedisSentTaskMailVO redisSentTaskMailVO) throws MessagingException, S3DataDownloadException, IOException;
    boolean sendTaskErrorMail(String address, RedisSentTaskMailVO redisSentTaskMailVO) throws MessagingException;





}
