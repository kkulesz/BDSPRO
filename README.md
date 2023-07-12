# Big Data Systems Project

everything that is written below is work in progress, it may slightly change when some problems occur.
## Project structure/packages:
- `dataGeneration` - module for generating data / can be written in different language
- `datasets` - wrapper for dataset, prepares dataset for later use, like dropping columns and unifies it somehow. Also stores some basic information about it, like name, size etc.
- `query` - module responsible for creating queries in unified way for SQL and Q languages. Each method in `QueryTranslator` interface is responsible for generating specific query type we want to benchmark.
- `databases` - module that includes one implementation of Database Interface for each database used (kdb+, influxDB, ...). `DBMeasurement` is a wrapper for performance measuring


#### Project deadline - 14th August

TODO:
- [ ] measure ingestion rate `Nils`
- [ ] configure experiments
  - [x] class that runs Benchmark on repeat with different values for dataset, batchsize, writePercentage etc.) `Konrad`
  - [x] log performance measurement somehow. JSON format `Dennis`
  - [ ] think about visualization of those `Dennis`
- [ ] run experiment on out local machines
- [ ] Docker Images `Konrad`
- [ ] tweak selectivity to get meaningful results
- [ ] report:
  - [ ] plan structure of report (how many pages for what?)
  - [ ] everybody write section about their database (low prio for now)


- structure of this report
  - similar to the midterm presentation (databases, datasets, why this benchmark, background: intro each db, compare) plus the results
  - TU thesis template, length min. 40 pages
- how to handle docker
  - one dockerfile or multiple for each database -> multiple, but all in one is ok, but make sure that only one is running at a time
    - one is better tho, because otherwise the environment might be different

Questions:
- How to visualize? By what parameters? Should we differentiate between write and read latency?
- specific plot types (what to be on what axis)
- can we use Python for visualization

- plot for each dataset 3 different plots.
  - plot 1: 
    - x-axis: writePercentage
    - y-axis average latency
    - for each value, have bar for each db
    - fix batch size to specific value
  - plot 2:
    - x-axis: selectivity
    - y-axis: latency
    - only for benchmark runs with write% = 0
    - fix batch size again
  - plot 3:
    - x-axis: ?
    - y-axis: latency or ingestion rate
    - only for benchmark runs with write% = 100
    - fix batch size
  - CHECK: how can we ensure we measure execution time of actual write -> query call might return before actual write happened

for selectivity, we should use buckets like 0 rows, 1 row, <0.01%, 0.01 - 0.1%, 0.1 - 1%, 1-10%, 10-100%