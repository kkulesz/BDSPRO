FROM openjdk:17
COPY out/artifacts/bdspro_jar/bdspro.jar BDSPRO.jar
COPY dataFiles/2019_c.csv dataFiles/2019_c.csv
COPY dataFiles/taxi_rides.csv dataFiles/taxi_rides.csv
ENTRYPOINT ["java","-jar","./BDSPRO.jar"]