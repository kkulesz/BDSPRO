FROM openjdk:17
COPY out/artifacts/bdspro_jar/bdspro.jar BDSPRO.jar
COPY TestData/2019_c.csv dataFiles/2019_c.csv
COPY TestData/taxi_rides.csv dataFiles/taxi_rides.csv
ENTRYPOINT ["java","-jar","./BDSPRO.jar"]