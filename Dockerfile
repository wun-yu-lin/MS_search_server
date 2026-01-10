# ==================== step 1: build ====================
FROM maven:3.8.1-openjdk-17 AS build
WORKDIR /app

COPY pom.xml .

COPY src ./src

# package
RUN mvn clean package -DskipTests

# ==================== step 2: run ====================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# create non root user
RUN addgroup -g 1001 appuser && \
    adduser -D -u 1001 -G appuser appuser

# cope jar
COPY --from=build /app/target/*.jar app.jar

# point：create dir for K8s ConfigMap mount
RUN mkdir -p /app/config && \
    chown -R appuser:appuser /app

# switch user
USER appuser

EXPOSE 8080

# 重點：不指定 spring.config.location，讓 Spring Boot 自動掃描
# Spring Boot 會按順序查找配置：
# 1. /app/config/application.yaml (K8s ConfigMap 掛載)
# 2. jar 內的 application.yaml (如果有)
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]