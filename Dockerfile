FROM openjdk:17
COPY out/artifacts/BDSPRO_jar/BDSPRO.jar BDSPRO.jar
ENTRYPOINT ["java","-jar","/BDSPRO.jar"]