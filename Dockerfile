FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /app

COPY LMS/pom.xml ./
RUN mvn dependency:go-offline

COPY LMS/src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/LMS-0.0.1-SNAPSHOT.jar ./LMS-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "LMS-0.0.1-SNAPSHOT.jar"]
