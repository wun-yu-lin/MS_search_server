package service.ms_search_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import sdk.mssearch.javasdk.EnableWebSdk;

@SpringBootApplication
@EnableScheduling
@EnableWebSdk
public class MsSearchEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsSearchEngineApplication.class, args);
    }

}
