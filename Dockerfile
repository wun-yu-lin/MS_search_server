FROM openjdk:17-jdk-slim
VOLUME /tmp
ADD target/MS_search_engine-0.0.2-SNAPSHOT.jar app-0.0.1.jar
ADD data/application.properties /app/config/application.properties
ADD
EXPOSE 8080
LABEL authors="linwunyu"
ENTRYPOINT ["java","-jar","app-0.0.1.jar", "--spring.config.name=application", "--spring.config.location=/app/config/"]