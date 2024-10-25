FROM openjdk:17-jdk-slim
LABEL authors="AlexanderPC"
WORKDIR /TechRadarPolls
COPY target/TechRadarPolls-0.0.1-SNAPSHOT.war TechRadarPolls-0.0.1-SNAPSHOT.war
ENTRYPOINT ["java", "-jar", "TechRadarPolls-0.0.1-SNAPSHOT.war"]