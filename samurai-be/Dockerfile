FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /samurai-be

# Install necessary packages
RUN apk add --no-cache bash

COPY gradle ./gradle
COPY build.gradle ./
COPY settings.gradle ./
COPY gradlew ./

# Ensure proper line endings and make gradlew executable
RUN sed -i 's/\r$//' gradlew && \
    chmod +x gradlew

RUN ./gradlew dependencies

COPY src src
RUN ./gradlew build -x test -x spotlessCheck -x spotlessApply

RUN java -Djarmode=layertools -jar build/libs/samurai-be-0.0.1-SNAPSHOT.jar extract --destination target/extracted

FROM eclipse-temurin:21-jre-alpine

WORKDIR /samurai-be

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /samurai-be/target/extracted/dependencies/ ./
COPY --from=builder /samurai-be/target/extracted/spring-boot-loader/ ./
COPY --from=builder /samurai-be/target/extracted/snapshot-dependencies/ ./
COPY --from=builder /samurai-be/target/extracted/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]