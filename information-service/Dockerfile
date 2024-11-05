FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /build

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/information-service-0.0.1-SNAPSHOT.jar ./information-service-0.0.1-SNAPSHOT.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "information-service-0.0.1-SNAPSHOT.jar"]
