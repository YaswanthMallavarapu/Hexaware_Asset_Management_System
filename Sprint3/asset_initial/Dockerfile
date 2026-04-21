# FROM : JVM for app compile
FROM maven:3.9.6-eclipse-temurin-21
# VOLUME - temp directory for docker to mount uour APP
VOLUME /tmp
# COPY - compy the jar file target/jar-file to .
COPY target/demo-0.0.1-SNAPSHOT.jar asset-backend.jar
# Command - java -jar <app-name>
ENTRYPOINT ["java", "-jar", "asset-backend.jar"]