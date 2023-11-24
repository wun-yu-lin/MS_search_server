package service.ms_search_engine.sentMail;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.ms_search_engine.constant.TaskStatus;
import service.ms_search_engine.redisService.RedisSentTaskMailVO;

import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/api/test/")
public class TestController {

    @Autowired
    private sentMailService sentMailService;

    @RequestMapping("test")
    public String test() throws  MessagingException {
        sentMailService.sendMailWithText("t928394558@gmail.com", "MS search team notice", "Not thing");

        RedisSentTaskMailVO redisSentTaskMailVO = new RedisSentTaskMailVO();
        redisSentTaskMailVO.setMailAddress("testAddress");
        redisSentTaskMailVO.setSubject("testSubject");
        redisSentTaskMailVO.setMainText("testMainText");
        redisSentTaskMailVO.setPeakListS3FileSrc("https://pgw.udn.com.tw/gw/photo.php?u=https://uc.udn.com.tw/photo/2021/11/05/0/14494019.png&x=0&y=0&sw=0&sh=0&sl=W&fw=1050");
        redisSentTaskMailVO.setMs2S3FileSrc("https://pgw.udn.com.tw/gw/photo.php?u=https://uc.udn.com.tw/photo/2021/11/05/0/14494019.png&x=0&y=0&sw=0&sh=0&sl=W&fw=1050");
        redisSentTaskMailVO.setResultPeakListS3FileSrc("https://pgw.udn.com.tw/gw/photo.php?u=https://uc.udn.com.tw/photo/2021/11/05/0/14494019.png&x=0&y=0&sw=0&sh=0&sl=W&fw=1050");
        redisSentTaskMailVO.setTaskId(0);
        redisSentTaskMailVO.setMsTolerance(0.0);
        redisSentTaskMailVO.setMsmsTolerance(0.0);
        redisSentTaskMailVO.setSimilarityTolerance(0.0);
        redisSentTaskMailVO.setForwardWeight(0.0);
        redisSentTaskMailVO.setReverseWeight(0.0);
        redisSentTaskMailVO.setSimilarityAlgorithm("testSimilarityAlgorithm");
        redisSentTaskMailVO.setIonMode("testIonMode");
        redisSentTaskMailVO.setTaskStatus(TaskStatus.SUBMIT_IN_WAITING);
        redisSentTaskMailVO.setMs1Ms2matchMzTolerance(0.0);
        redisSentTaskMailVO.setMs1Ms2matchRtTolerance(0.0);
        redisSentTaskMailVO.setFinishTime(new Date());
        redisSentTaskMailVO.setTaskDescription("testTaskDescription");

        sentMailService.sendTaskSubmitMail("t928394558@gmail.com", redisSentTaskMailVO);
        sentMailService.sendTaskInProcessMail("t928394558@gmail.com", redisSentTaskMailVO);
        sentMailService.sendTaskFinishMail("t928394558@gmail.com", redisSentTaskMailVO);
        sentMailService.sendTaskErrorMail("t928394558@gmail.com", redisSentTaskMailVO);





        return "sendMailWithText";
    }

}
