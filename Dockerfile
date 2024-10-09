FROM openjdk:17-jdk
VOLUME /tmp
ADD target/ms_search_engine-SNAPSHOT.jar app.jar
ADD src/main/resources/application.properties /app/config/application.properties
EXPOSE 8080
LABEL authors="linwunyu"

ENTRYPOINT ["java","-jar","app.jar", "--spring.config.name=application", "--spring.config.location=/app/config/"]
