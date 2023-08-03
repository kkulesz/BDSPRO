#!/bin/bash

# Create arrays with values
writeFrequency=(0 100)
writePercentage=(0 25 50 75 100)
batchSize=(1000)
datasets=("Climate" "Taxi")
numQueries=(1000)
databases=("Clickhouse" "TimescaleDB")

# Function to wait for the specific message in logs
function wait_for_message_in_logs() {
    container_name="$1"
    message="$2"

    echo "Waiting for message in logs: $message"
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
                echo "Starting TimescaleDB"
                docker compose up --build -d timescaledb 1> /dev/null
                wait_for_message_in_logs bdspro-timescaledb-1 "database system is ready to accept connections"
                docker exec -it bdspro-timescaledb-1 psql -U timescaledb -c "CREATE DATABASE bdspro;"
                docker exec -it bdspro-timescaledb-1 psql -U timescaledb -d bdspro -c "CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;"
            elif [ "$db" == "Clickhouse" ]; then
                echo "Starting Clickhouse"
                docker compose up --build -d clickhouse 1> /dev/null
                wait_for_message_in_logs bdspro-clickhouse-1 "create database 'benchmark'"
            fi

            # prepare parameters
            echo "counter=$counter" > parameters.txt
            echo "wf=$wf" >> parameters.txt
            echo "wp=$wp" >> parameters.txt
            echo "bs=$bs" >> parameters.txt
            echo "ds=$ds" >> parameters.txt
            echo "nq=$nq" >> parameters.txt
            echo "db=$db" >> parameters.txt


            docker compose up --no-deps --build benchmark_single

            echo "Tearing all containers down"
            docker compose down -v 1> /dev/null

          done
        done
      done
    done
  done
done
