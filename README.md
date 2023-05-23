# Big Data Systems Project

everything that is written below is work in progress, it may slightly change when some problems occur.
## Project structure/packages:
- `dataGeneration` - module for generating data / can be written in different language
- `datasets` - wrapper for dataset, prepares dataset for later use, like dropping columns and unifies it somehow. Also stores some basic information about it, like name, size etc.
- `query` - module responsible for creating queries in unified way for SQL and Q languages. Each method in `QueryTranslator` interface is responsible for generating specific query type we want to benchmark.
- `databases` - module that includes one implementation of Database Interface for each database used (kdb+, influxDB, ...). `DBMeasurement` is a wrapper for performance measuring



TODO:
- come up with structure of benchmark run function
- design main class and think about which benchmarks (which parameters)
- implement one DB Adapter
- think about data generators



questions:
- can we modify datasets we use?
  - like drop columns and choose only those of specific type in order to unify handling them.
- should one benchmark use several datasets (+ generated data)
- which metrics to use