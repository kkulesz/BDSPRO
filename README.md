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



problems so far:
- `QueryTranslator`s should be static? then we cannot have interfaces for them, but is that an issue?
- Dataset interface? we should introduce it i think. Creating queries would be easier
- should we load dataset in code or can we do it by hand if database has such functionality? Maybe let's start without and if we have time then we can  add it
- maybe change `QueryTranslator` into `<language>Builder`, e.g. `SqlQueryTranslator`->`SqlBuilder` and make it also build `CREATE TABLE`, `INSERT INTO`, etc. statements. 
- I really think that `DBMeasurement` shouldn't implement `Database` interface. It will be only instance of such object and we can manage without this dependency
- also - should `Database` get prepared queries/statements as a function argument? Like it should only connect to DB, insert data into '?' in given queries and execute so we measure only this. Query/statement formation should be outside it's functions

Notes:
- command for TimescaleDB to copy data into table `psql -U postgres -d bdspro -c "\COPY test_table FROM test.csv delimiter ',' CSV HEADER"`. Maybe there is something similar in other DBs