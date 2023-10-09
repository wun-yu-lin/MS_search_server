package service.ms_search_engine.IocTest.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfiguration {

    @Bean
    public MySQL_configuration create_mysql_config_class(){
         return new MySQL_configuration("test", 3306, "test", "username", "password");
    };


}


