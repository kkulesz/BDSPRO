#!/bin/bash

# Create arrays with values
writeFrequency=(0)
writePercentage=(50)
batchSize=(1000)
datasets=("Climate")
numQueries=(10)
databases=("Clickhouse")

# host names of database
dbHosts=("cloud-41" "cloud-42" "cloud-43")

# Function to wait for the specific message in logs
function wait_for_message_in_logs() {
    container_name="$1"
    message="$2"
    host="$3"

    echo "Waiting for message in logs: $message, on host: $host"
    while ! ssh $host "docker logs \"$container_name\""| grep -q "$message"; do
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
                echo "Starting TimescaleDB"
                docker compose up --build -d timescaledb 1> /dev/null
                wait_for_message_in_logs bdspro-timescaledb-1 "database system is ready to accept connections"
                docker exec -it bdspro-timescaledb-1 psql -U timescaledb -c "CREATE DATABASE bdspro;"
                docker exec -it bdspro-timescaledb-1 psql -U timescaledb -d bdspro -c "CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;"
            elif [ "$db" == "Clickhouse" ]; then
              for host in "${dbHosts[@]}"; do
                  echo "Starting Clickhouse node $host"
                  ssh $host "docker run -d --name db --ulimit nofile=262144:262144 -p \"8124:8123\" -p \"9001:9000\" -v /home/dnatusch/config/clickhouse_multi_node.xml:/etc/clickhouse-server/config.xml -v /home/dnatusch/config/hosts:/etc/hosts -e CLICKHOUSE_USER=bdspro -e CLICKHOUSE_PASSWORD=password -e CLICKHOUSE_DEFAULT_ACCESS_MANAGEMENT=1 -e CLICKHOUSE_DB=benchmark --rm -m 4G --cpus 2 clickhouse/clickhouse-server"
                  wait_for_message_in_logs db "create database 'benchmark'" "$host"
              done
            fi

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
            docker run -it --rm --name benchmark_run --mount type=bind,source=/home/dnatusch/results,target=/results benchmark

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


