# Big Data Systems Project

everything that is written below is work in progress, it may slightly change when some problems occur.
## Project structure/packages:
- `dataGeneration` - module for generating data / can be written in different language
- `datasets` - wrapper for dataset, prepares dataset for later use, like dropping columns and unifies it somehow. Also stores some basic information about it, like name, size etc.
- `query` - module responsible for creating queries in unified way for SQL and Q languages. Each method in `QueryTranslator` interface is responsible for generating specific query type we want to benchmark.
- `databases` - module that includes one implementation of Database Interface for each database used (kdb+, influxDB, ...). `DBMeasurement` is a wrapper for performance measuring


#### Project deadline - 14th August

TODO:
- [x] plot for write only workloads
- [x] fix compression rate for TSDB
- [ ] add section in report about COmpression rate
- [ ] resolve todos in report
- [ ] run experiment on multiple nodes

- plot for each dataset 3 different plots.
  - plot 3:
    - x-axis: ?
    - y-axis: latency or ingestion rate
    - only for benchmark runs with write% = 100
    - fix batch size
