FROM openjdk:17
COPY out/artifacts/bdspro_jar2/bdspro.jar BDSPRO.jar
COPY dataFiles/2019_c.csv dataFiles/2019_c.csv
COPY dataFiles/taxi_rides.csv dataFiles/taxi_rides.csv
COPY parameters.txt /parameters.txt
COPY config/hosts /etc/hosts
ENTRYPOINT ["java","-jar","./BDSPRO.jar"]