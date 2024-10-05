package service.ms_search_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MsSearchEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsSearchEngineApplication.class, args);
    }

}
