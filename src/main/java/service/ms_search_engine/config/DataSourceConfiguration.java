package service.ms_search_engine.config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    // 連線到 mySQL database datasource and NamedParameterJdbcTemplate
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.mysql-local")
    public DataSource mysqlLocalDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean
    public NamedParameterJdbcTemplate mysqlJdbcNamedParameterTemplate(
            @Qualifier("mysqlLocalDataSource") DataSource dataSource
    ){
        return new NamedParameterJdbcTemplate(dataSource);
    }
}