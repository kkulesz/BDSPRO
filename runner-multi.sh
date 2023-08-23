#!/bin/bash

homeDirectory="kkulesza"
#homeDirectory="dnatusch"

# Create arrays with values
datasets=("Taxi")
databases=("TimescaleDB")
#datasets=("Climate")
#databases=("Clickhouse")

writeFrequency=(0)
writePercentage=(50)
batchSize=(1000)
numQueries=(10)

# host names of database
dbHosts=("cloud-42")
#dbHosts=("cloud-41" "cloud-42" "cloud-43")

# Function to wait for the specific message in logs
function wait_for_message_in_logs() {
    container_name="$1"
    message="$2"
    host="$3"

    echo "Waiting for message in logs: \"$message\" on host: $host"
    while ! ssh $host "docker logs \"$container_name\""| grep -q "$message"; do
        sleep 20
    done
    echo "Message found: $message"
}

function wait_for_message_in_logs_locally() {
    container_name="$1"
    message="$2"

    echo "Waiting for message in LOCAL logs: $message"
    while ! docker logs "$container_name" | grep -q "$message"; do
        sleep 1
    done
    echo "Message found: $message"
}

# Nested for loops
counter=0
for wf in "${writeFrequency[@]}"; do
  for wp in "${writePercentage[@]}"; do
    for bs in "${batchSize[@]}"; do
      for ds in "${datasets[@]}"; do
        for nq in "${numQueries[@]}"; do
          for db in "${databases[@]}"; do
            ((counter++))
            echo "Running benchmark $counter with configuration: wf=$wf, wp=$wp, bs=$bs, ds=$ds, nq=$nq, db=$db"

            # start database container and wait for boot
            if [ "$db" == "TimescaleDB" ]; then

                for host in "${dbHosts[@]}"; do
                  echo "Starting TimescaleDB data node $host"
                  ssh $host "docker run -d --name db  -p \"5433:5432\" -e POSTGRES_USER=timescaledb -e POSTGRES_PASSWORD=password -e POSTGRES_DATABASE=bdspro --rm -m 4G --cpus 2 timescale/timescaledb:latest-pg12"
                  wait_for_message_in_logs db "database system is ready to accept connections" $host
                  ssh $host "docker exec db psql -U timescaledb -c \"CREATE DATABASE bdspro;\""
                  ssh $host "docker exec db psql -U timescaledb -d bdspro -c \"CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;\""
                done

                echo "Starting TimescaleDB access node locally"
                docker-compose up --build -d timescaledb 1> /dev/null
                wait_for_message_in_logs_locally ${homeDirectory}_timescaledb_1 "database system is ready to accept connections"
                docker exec -it ${homeDirectory}_timescaledb_1 psql -U timescaledb -c "CREATE DATABASE bdspro;"
                docker exec -it ${homeDirectory}_timescaledb_1 psql -U timescaledb -d bdspro -c "CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;"
            elif [ "$db" == "Clickhouse" ]; then
              for host in "${dbHosts[@]}"; do
                  echo "Starting Clickhouse node $host"
                  ssh $host "docker run -d --name db --ulimit nofile=262144:262144 -p \"8124:8123\" -p \"9001:9000\" -v /home/$homeDirectory/config/clickhouse_multi_node.xml:/etc/clickhouse-server/config.xml -v /home/$homeDirectory/config/hosts:/etc/hosts -e CLICKHOUSE_USER=bdspro -e CLICKHOUSE_PASSWORD=password -e CLICKHOUSE_DEFAULT_ACCESS_MANAGEMENT=1 -e CLICKHOUSE_DB=benchmark --rm -m 4G --cpus 2 clickhouse/clickhouse-server"
                  wait_for_message_in_logs db "create database 'benchmark'" "$host"
              done
            fi

            echo "NODES CONFIGURED..."

            # prepare parameters
            echo "counter=$counter" > parameters.txt
            echo "wf=$wf" >> parameters.txt
            echo "wp=$wp" >> parameters.txt
            echo "bs=$bs" >> parameters.txt
            echo "ds=$ds" >> parameters.txt
            echo "nq=$nq" >> parameters.txt
            echo "db=$db" >> parameters.txt
            echo "non=3" >> parameters.txt


            docker build -t benchmark .
            docker run -it --rm --name benchmark_run --mount type=bind,source=/home/$homeDirectory/results,target=/results benchmark

            echo "Tearing all containers down"
             for host in "${dbHosts[@]}"; do
                echo "Tearing down all container on node $host"
                ssh $host "docker container stop db"
            done

          done
        done
      done
    done
  done
done


