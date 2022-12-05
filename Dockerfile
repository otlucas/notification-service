FROM gradle:6.8.3-jdk11 as build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

COPY src ./src

RUN gradle build

FROM openjdk:11-jre-slim

COPY --from=build /app/build/libs/notification-service.jar .

EXPOSE 8080

CMD ["java", "-jar", "notification-service.jar"]