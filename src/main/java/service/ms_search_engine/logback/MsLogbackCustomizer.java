package service.ms_search_engine.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import sdk.mssearch.javasdk.elk.LogbackCustomizer;
import service.ms_search_engine.MsSearchEngineApplication;

@Component
public class MsLogbackCustomizer implements LogbackCustomizer {
    @Override
    public void customize(LoggerContext loggerContext) {
        loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO);
        loggerContext.getLogger(MsSearchEngineApplication.class.getPackageName()).setLevel(Level.INFO);
    }
}
