FROM openjdk:17-jdk-slim
VOLUME /tmp
ADD target/MS_search_engine-1.0.6-SNAPSHOT.jar app.jar
ADD src/main/resources/application.properties /app/config/develop/application.properties
ADD src/main/resources/application-production-webServer.properties /app/config/production/webServer/application.properties
ADD src/main/resources/application-production-backgroundServer.properties /app/config/production/backgroundServer/application.properties
EXPOSE 8080
LABEL authors="linwunyu"
ENTRYPOINT ["java","-jar","app.jar", "--spring.config.name=application", "--spring.config.location=/app/config/production/webServer/"]
