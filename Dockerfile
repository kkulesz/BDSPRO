FROM openjdk:17
COPY target/bdspro-1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]