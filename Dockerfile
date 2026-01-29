FROM gradle:8.7.0-jdk21 AS build

WORKDIR /app

COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./

RUN ./gradlew --no-daemon dependencies

COPY src src

RUN ./gradlew --no-daemon clean bootJar -x test

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","/app/app.jar"]
