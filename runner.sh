#!/bin/bash

# Create arrays with values
writeFrequency=(1000)
writePercentage=(0 50 100)
batchSize=(1000)
datasets=("Climate" "Taxi")
numQueries=(1000)
databases=("TimescaleDB" "Clickhouse")

# Function to wait for the specific message in logs
function wait_for_message_in_logs() {
    container_name="$1"
    message="$2"

    echo "Waiting for message in logs: $message"
    while ! docker logs "$container_name" | grep -q "$message"; do
        sleep 1
    done
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
                docker compose up --build -d timescaledb
                wait_for_message_in_logs bdspro-timescaledb-1 "database system is ready to accept connections"
                docker exec -it bdspro-timescaledb-1 psql -U timescaledb -c "CREATE DATABASE bdspro;"
            elif [ "$db" == "Clickhouse" ]; then
                docker compose up --build -d clickhouse
                wait_for_message_in_logs bdspro-clickhouse-1 "create database 'benchmark'"
            fi
            echo "counter=$counter" > parameters.txt
            echo "wf=$wf" >> parameters.txt
            echo "wp=$wp" >> parameters.txt
            echo "bs=$bs" >> parameters.txt
            echo "ds=$ds" >> parameters.txt
            echo "nq=$nq" >> parameters.txt
            echo "db=$db" >> parameters.txt


            docker compose up --no-deps --build benchmark_single

            docker compose down -v

          done
        done
      done
    done
  done
done
