FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /app

COPY user-service/pom.xml ./
RUN mvn dependency:go-offline

COPY user-service/src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/user-service-0.0.1-SNAPSHOT.jar ./user-service-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "user-service-0.0.1-SNAPSHOT.jar"]
