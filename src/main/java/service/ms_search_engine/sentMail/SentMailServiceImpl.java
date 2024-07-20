package service.ms_search_engine.sentMail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import service.ms_search_engine.dao.BatchSearchS3FileDao;
import service.ms_search_engine.exception.S3DataDownloadException;
import service.ms_search_engine.redisService.RedisSentTaskMailVO;

import java.io.File;
import java.io.IOException;

@Component
public class SentMailServiceImpl implements SentMailService{
    private final JavaMailSender javaMailSender;
    private final BatchSearchS3FileDao batchSearchS3FileDao;

    @Value("${spring.mail.username}")
    private String username;

    private static final String headImg = "<img style=\"width: 30px; height: 30px;\" src=\"https://purple-cold-033.notion.site/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F3bbb6125-1f4c-4a9e-8239-419e9cd63397%2F7a768929-d7f0-442c-95d6-f7dc0c9e8ed8%2FwebIcon.png?table=block&id=69d8d0e5-24cb-4127-a1ae-6b17b7cab6b7&spaceId=3bbb6125-1f4c-4a9e-8239-419e9cd63397&width=250&userId=&cache=v2\">";
    private static final String head = "<h3>" +headImg+  "  MS search task notice mail</h3> <br>";
    private static final String footer ="__________________ <br>"+ "Best regards, <br>" + "MS search team , <br>" + "<a href='https://ms-search.us'>ms-search</a> <br>" ;

    @Autowired
    public SentMailServiceImpl(JavaMailSender javaMailSender, BatchSearchS3FileDao batchSearchS3FileDao) {
        this.javaMailSender = javaMailSender;
        this.batchSearchS3FileDao = batchSearchS3FileDao;
    }


    @Override
    public boolean sendMailWithText(String address, String subject, String text) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        //String headImg = "<img style=\"width: 30px; height: 30px;\" src=\"https://purple-cold-033.notion.site/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F3bbb6125-1f4c-4a9e-8239-419e9cd63397%2F7a768929-d7f0-442c-95d6-f7dc0c9e8ed8%2FwebIcon.png?table=block&id=69d8d0e5-24cb-4127-a1ae-6b17b7cab6b7&spaceId=3bbb6125-1f4c-4a9e-8239-419e9cd63397&width=250&userId=&cache=v2\">";
        //String head = "<h3>" +headImg+  "  MS search task notice mail</h3> <br>";

        String content ="<p>" +  text + "</P>" + "<br>";

        //String footer ="__________________ <br>"+ "Best regards, <br>" + "MS search team , <br>" + "<a href='https://ms-search.us'>ms-search</a> <br>" ;

        String htmlMsg = this.head + content + this.footer;
        helper.setText(htmlMsg, true); // Use this or above line.
        helper.setTo(address);
        helper.setSubject(subject);
        helper.setFrom(username);
        javaMailSender.send(mimeMessage);

        return true;


    }

    @Override
    public boolean sendTaskSubmitMail(String address, RedisSentTaskMailVO redisSentTaskMailVO) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");;
        if (redisSentTaskMailVO.getMailAddress() == null){throw new MessagingException("mail address is null");}
        if (redisSentTaskMailVO.getTaskId() == null){throw new MessagingException("task id is null");}
        if (redisSentTaskMailVO.getTaskDescription() == null){redisSentTaskMailVO.setTaskDescription("no description");}
        if (redisSentTaskMailVO.getPeakListS3FileSrc()==null){redisSentTaskMailVO.setPeakListS3FileSrc("N/A");}
        if (redisSentTaskMailVO.getMs2S3FileSrc()==null){redisSentTaskMailVO.setMs2S3FileSrc("N/A");}
        if (redisSentTaskMailVO.getIonMode() == null ||
            redisSentTaskMailVO.getIonMode().equals("")
        ){redisSentTaskMailVO.setIonMode("All");}


        String content = "<p>Dear,</p>" +
                "<p>Thank you for submitting your task to MS search. Your task has been submitted successfully. </p>" +
                "<p>Task ID: " + redisSentTaskMailVO.getTaskId() + "</p>" +
                "<p>Task description: " + redisSentTaskMailVO.getTaskDescription() + "</p>" +
                "<p>Task submitter email: " + redisSentTaskMailVO.getMailAddress() + "</p>" +
                "<p>Task status: " +  redisSentTaskMailVO.getTaskStatus().getDescription() + "</p>" +
                "<p>Task file url are as follows.</p>" +
                "<p>Peak list file url: " + redisSentTaskMailVO.getPeakListS3FileSrc() + "</p>" +
                "<p>MS2 file url: " + redisSentTaskMailVO.getMs2S3FileSrc() + "</p>" +
                "<hr>" +
                "<p>Task parameters are as follows.</p>" +
                "<p>MS tolerance: " + redisSentTaskMailVO.getMsTolerance() + "</p>" +
                "<p>MSMS tolerance: " + redisSentTaskMailVO.getMsmsTolerance() + "</p>" +
                "<p>Similarity tolerance: " + redisSentTaskMailVO.getSimilarityTolerance() + "</p>" +
                "<p>Forward weight: " + redisSentTaskMailVO.getForwardWeight() + "</p>" +
                "<p>Reverse weight: " + redisSentTaskMailVO.getReverseWeight() + "</p>" +
                "<p>Similarity algorithm: " + redisSentTaskMailVO.getSimilarityAlgorithm() + "</p>" +
                "<p>Ion mode: " + redisSentTaskMailVO.getIonMode() + "</p>" +
                "<p>MS1 MS2 match mz tolerance: " + redisSentTaskMailVO.getMs1Ms2matchMzTolerance() + "</p>" +
                "<p>MS1 MS2 match rt tolerance: " + redisSentTaskMailVO.getMs1Ms2matchRtTolerance() + "</p>" +
                "<hr>" +
                "<p>Task result will be sent to your email when the task is finished.</p>" +
                "<p>Thank you for using MS search.</p>" +
                "<br>";

        //String footer ="__________________ <br>"+ "Best regards, <br>" + "MS search team , <br>" + "<a href='https://ms-search.us'>ms-search</a> <br>" ;

        String htmlMsg = this.head + content + this.footer;
        helper.setText(htmlMsg, true); // Use this or above line.
        helper.setTo(address);
        helper.setSubject("MS search task submit successful notice."+ " Task ID: " + redisSentTaskMailVO.getTaskId());
        helper.setFrom(username);
        javaMailSender.send(mimeMessage);

        return true;
    }

    @Override
    public boolean sendTaskInProcessMail(String address, RedisSentTaskMailVO redisSentTaskMailVO) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");;
        if (redisSentTaskMailVO.getMailAddress() == null){throw new MessagingException("mail address is null");}
        if (redisSentTaskMailVO.getTaskId() == null){throw new MessagingException("task id is null");}
        if (redisSentTaskMailVO.getTaskDescription() == null){redisSentTaskMailVO.setTaskDescription("no description");}
        if (redisSentTaskMailVO.getPeakListS3FileSrc()==null){redisSentTaskMailVO.setPeakListS3FileSrc("N/A");}
        if (redisSentTaskMailVO.getMs2S3FileSrc()==null){redisSentTaskMailVO.setMs2S3FileSrc("N/A");}
        if (redisSentTaskMailVO.getIonMode() == null ||
                redisSentTaskMailVO.getIonMode().equals("")
        ){redisSentTaskMailVO.setIonMode("All");}


        String content = "<p>Dear,</p>" +
                "<p>Your task is currently in progress. Please pay attention to subsequent task status messages.  </p>" +
                "<p>Task ID: " + redisSentTaskMailVO.getTaskId() + "</p>" +
                "<p>Task description: " + redisSentTaskMailVO.getTaskDescription() + "</p>" +
                "<p>Task submitter email: " + redisSentTaskMailVO.getMailAddress() + "</p>" +
                "<p>Task status: " +  redisSentTaskMailVO.getTaskStatus().getDescription() + "</p>" +
                "<p>Task file url are as follows.</p>" +
                "<p>Peak list file url: " + redisSentTaskMailVO.getPeakListS3FileSrc() + "</p>" +
                "<p>MS2 file url: " + redisSentTaskMailVO.getMs2S3FileSrc() + "</p>" +
                "<hr>" +
                "<p>Task parameters are as follows.</p>" +
                "<p>MS tolerance: " + redisSentTaskMailVO.getMsTolerance() + "</p>" +
                "<p>MSMS tolerance: " + redisSentTaskMailVO.getMsmsTolerance() + "</p>" +
                "<p>Similarity tolerance: " + redisSentTaskMailVO.getSimilarityTolerance() + "</p>" +
                "<p>Forward weight: " + redisSentTaskMailVO.getForwardWeight() + "</p>" +
                "<p>Reverse weight: " + redisSentTaskMailVO.getReverseWeight() + "</p>" +
                "<p>Similarity algorithm: " + redisSentTaskMailVO.getSimilarityAlgorithm() + "</p>" +
                "<p>Ion mode: " + redisSentTaskMailVO.getIonMode() + "</p>" +
                "<p>MS1 MS2 match mz tolerance: " + redisSentTaskMailVO.getMs1Ms2matchMzTolerance() + "</p>" +
                "<p>MS1 MS2 match rt tolerance: " + redisSentTaskMailVO.getMs1Ms2matchRtTolerance() + "</p>" +
                "<hr>" +
                "<p>Task result will be sent to your email when the task is finished.</p>" +
                "<p>Thank you for using MS search.</p>" +
                "<br>";

        //String footer ="__________________ <br>"+ "Best regards, <br>" + "MS search team , <br>" + "<a href='https://ms-search.us'>ms-search</a> <br>" ;

        String htmlMsg = this.head + content + this.footer;
        helper.setText(htmlMsg, true); // Use this or above line.
        helper.setTo(address);
        helper.setSubject("MS search task in processing successful notice." + " Task ID: " + redisSentTaskMailVO.getTaskId());
        helper.setFrom(username);
        javaMailSender.send(mimeMessage);

        return true;
    }

    @Override
    public boolean sendTaskFinishMail(String address, RedisSentTaskMailVO redisSentTaskMailVO) throws MessagingException, S3DataDownloadException, IOException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
        if (redisSentTaskMailVO.getMailAddress() == null){throw new MessagingException("mail address is null");}
        if (redisSentTaskMailVO.getTaskId() == null){throw new MessagingException("task id is null");}
        if (redisSentTaskMailVO.getTaskDescription() == null){redisSentTaskMailVO.setTaskDescription("no description");}
        if (redisSentTaskMailVO.getPeakListS3FileSrc()==null){redisSentTaskMailVO.setPeakListS3FileSrc("N/A");}
        if (redisSentTaskMailVO.getMs2S3FileSrc()==null){redisSentTaskMailVO.setMs2S3FileSrc("N/A");}
        if (redisSentTaskMailVO.getIonMode() == null ||
                redisSentTaskMailVO.getIonMode().equals("")
        ){redisSentTaskMailVO.setIonMode("All");}


        String content = "<p>Dear,</p>" +
                "<p>Your task Finish! Please download file by URL  </p>" +
                "<p>Task ID: " + redisSentTaskMailVO.getTaskId() + "</p>" +
                "<p>Task description: " + redisSentTaskMailVO.getTaskDescription() + "</p>" +
                "<p>Result peak list file url: " + redisSentTaskMailVO.getResultPeakListS3FileSrc() + "</p>" +
                "<hr>" +
                "<p>Task parameters are as follows.</p>" +
                "<p>MS tolerance: " + redisSentTaskMailVO.getMsTolerance() + "</p>" +
                "<p>MSMS tolerance: " + redisSentTaskMailVO.getMsmsTolerance() + "</p>" +
                "<p>Similarity tolerance: " + redisSentTaskMailVO.getSimilarityTolerance() + "</p>" +
                "<p>Forward weight: " + redisSentTaskMailVO.getForwardWeight() + "</p>" +
                "<p>Reverse weight: " + redisSentTaskMailVO.getReverseWeight() + "</p>" +
                "<p>Similarity algorithm: " + redisSentTaskMailVO.getSimilarityAlgorithm() + "</p>" +
                "<p>Ion mode: " + redisSentTaskMailVO.getIonMode() + "</p>" +
                "<p>MS1 MS2 match mz tolerance: " + redisSentTaskMailVO.getMs1Ms2matchMzTolerance() + "</p>" +
                "<p>MS1 MS2 match rt tolerance: " + redisSentTaskMailVO.getMs1Ms2matchRtTolerance() + "</p>" +
                "<hr>" +
                "<br>" +
                "<p>Thank you for using MS search.</p>" +
                "<br>";

        //String footer ="__________________ <br>"+ "Best regards, <br>" + "MS search team , <br>" + "<a href='https://ms-search.us'>ms-search</a> <br>" ;

        String htmlMsg = this.head + content + this.footer;
        helper.setText(htmlMsg, true); // Use this or above line.
        helper.setTo(address);
        helper.setSubject("MS search task finish notice." + " Task ID: " + redisSentTaskMailVO.getTaskId());
        helper.setFrom(username);

        //Download result file from s3, and set as byteArray output stream
        UrlResource attFileUrl = batchSearchS3FileDao.downloadFileByFileName(redisSentTaskMailVO.getResultPeakListS3FileSrc().split(".net/")[1]);
        File attFile = new File(attFileUrl.getURI().getPath());
        helper.addAttachment(attFile.getName(), attFile);






        javaMailSender.send(mimeMessage);
        File file = new File(attFileUrl.getURI());

        return file.delete();
    }

    @Override
    public boolean sendTaskErrorMail(String address, RedisSentTaskMailVO redisSentTaskMailVO) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String content ="<p>Task ID: " + redisSentTaskMailVO.getTaskId() + "</P>" + "<br>" +
                "<p>Task description: " + redisSentTaskMailVO.getTaskDescription() + "</P>" + "<br>" +
                "<p>Task error, please check uploaded file and parameter.</P>" + "<br>";

        //String footer ="__________________ <br>"+ "Best regards, <br>" + "MS search team , <br>" + "<a href='https://ms-search.us'>ms-search</a> <br>" ;

        String htmlMsg = this.head + content + this.footer;
        helper.setText(htmlMsg, true); // Use this or above line.
        helper.setTo(address);
        helper.setSubject("MS search task error notice." + " Task ID: " + redisSentTaskMailVO.getTaskId());
        helper.setFrom(username);
        javaMailSender.send(mimeMessage);
        return true;
    }


}
