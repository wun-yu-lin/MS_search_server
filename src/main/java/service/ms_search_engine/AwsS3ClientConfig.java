package service.ms_search_engine;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3ClientConfig {
    @Value("${aws.s3.accessKey}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secretKey}")
    private String awsS3SecretKey;

    @Bean
    public AmazonS3 initS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(this.awsS3AccessKey, this.awsS3SecretKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}
