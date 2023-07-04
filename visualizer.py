import json
from statistics import mean
import matplotlib.pyplot as plt

## returns list of tuples with read and write latency averages for database 'db_name', filtered by parameter value_name having value value
## e.g. filtering by batchSize == 10
def getAveragesForDBAndValue(json, db_name, value_name, value):
    read_results = []
    write_results = []
    for benchmark in json:
        if benchmark[value_name] == value:
            read_results = read_results + [res['latency'] for res in benchmark['readResults'][db_name]]
            write_results = write_results + [res['latency'] for res in benchmark['writeResults'][db_name]]
    return mean(read_results), mean(write_results)


## returns list of all values found in the json for the parameter 'value_name'
def getAllValues(json, value_name):
    values = []
    for benchmark in json:
        values.append(benchmark[value_name])
    values = sorted(set(values))
    return values

## returns list of all Databases used in the benchmark
def getAllDBs(json):
    return list(json[0]['readResults'].keys())

## displays a line chart with the readResult averages for each database on the y axis and with batchSize on the x axis
def visualize_batch_sizes(json):
    values = getAllValues(json, 'batchSize')
    dbs = getAllDBs(json)
    results = {}
    for db in dbs:
        results[db] = [getAveragesForDBAndValue(json, db, 'batchSize', v) for v in values]
    plt.plot(values, [res[0] for res in results['ClickHouse']])
    plt.show()
    print(results)


with open("benchmark_result") as json_file:
    json_result = json.load(json_file)
    visualize_batch_sizes(json_result)


