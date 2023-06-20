# Big Data Systems Project

everything that is written below is work in progress, it may slightly change when some problems occur.
## Project structure/packages:
- `dataGeneration` - module for generating data / can be written in different language
- `datasets` - wrapper for dataset, prepares dataset for later use, like dropping columns and unifies it somehow. Also stores some basic information about it, like name, size etc.
- `query` - module responsible for creating queries in unified way for SQL and Q languages. Each method in `QueryTranslator` interface is responsible for generating specific query type we want to benchmark.
- `databases` - module that includes one implementation of Database Interface for each database used (kdb+, influxDB, ...). `DBMeasurement` is a wrapper for performance measuring


#### Project deadline - 14th August

TODO:
- Databases:
  - [x] Clickhouse
  - [ ] TimeScaleDB
  - [ ] InfluxDB
  - [ ] Druid
- Datasets:
  - meaning create 4 classes similar to `datasets/TestDataset` that correspond to datasets we chose
- configure experiments
  - log performance measurement somehow. CSV format
  - think about visualization of those
- figure out how to measure compression
  - probably each database adapter should implement `getSize()` method
- decide on how to handle docker - ask dr Pandey?
  - one dockerfile or multiple for each database
- run experiment on out local machines
- report:
  - [ ] everybody write section about their database (low prio for now)
  - ask dr Pandey about structure of this report:
    - design main class and think about which benchmarks (which parameters)


Notes:
- command for TimescaleDB to copy data into table `psql -U postgres -d bdspro -c "\COPY test_table FROM test.csv delimiter ',' CSV HEADER"`. Maybe there is something similar in other DBs