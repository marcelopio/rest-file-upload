FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} rest-file-upload.jar
ENTRYPOINT ["java","-jar","/rest-file-upload.jar"]