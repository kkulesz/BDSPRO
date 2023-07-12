import json
from statistics import mean
import matplotlib.pyplot as plt
import numpy

## returns list of tuples with read and write latency averages for database 'db_name', filtered by parameter value_name having value value
## e.g. filtering by batchSize == 10
def getAveragesForDBAndValue(json, db_name, value_name, value):
    read_results = []
    write_results = []
    for benchmark in json:
        if benchmark[value_name] == value:
            read_results = read_results + [res['latency'] for res in benchmark['readResults'][db_name]]
            write_results = write_results + [res['latency'] for res in benchmark['writeResults'][db_name]]
    if (read_results == []):
        return 0, mean(write_results)
    if (write_results == []):
        return mean(read_results), 0
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

def filter(json, value_name, value):
    res = []
    for benchmark in json:
        if benchmark[value_name] == value:
            res.append(benchmark)
    return res

## displays a multiple bar chart with the latency results on the y axis and the writePercentage on the x axis
def visualize_write_percentages(json, batch_size):
    json = filter(json, "batchSize", batch_size)
    values = getAllValues(json, "writePercentage")
    values = sorted(values)
    dbs = getAllDBs(json)
    results_by_db = {}
    for db in dbs:
        means = []
        for wp in values:
            # TODO: write values are ignored right now
            r, _ = getAveragesForDBAndValue(json, db, "writePercentage", wp)
            means.append(r)
        results_by_db[db] = means
    print(results_by_db)
    x = numpy.array(values)
    ax = plt.subplot(111)
    ax.bar(x-1, results_by_db[dbs[0]], width=2, color='b', align='center')
   # ax.bar(x+1, results_by_db[dbs[1]], width=2, color='r', align='center')

    plt.show()


def visualize_read_only(json, row_count, batch_size):
    json = filter(json, "batchSize", batch_size)
    dbs = getAllDBs(json)
    results_by_db = {}
    buckets = [0, 1, 0.0001 * row_count, 0.001 * row_count, 0.01 * row_count, 0.1 * row_count, row_count]
    for db in dbs:
        latencies_by_selectivity = {}
        for bucket in buckets:
            latencies_by_selectivity[str(bucket)] = []
        for benchmark in json:
            res = benchmark["readResults"][db]
            for queryRes in res:
                for i in range(len(buckets)):
                    if queryRes["rowCount"] <= buckets[i] or i == len(buckets)-1:
                        latencies_by_selectivity[str(buckets[i])].append(queryRes["latency"])
                        break
        results_by_db[db] = latencies_by_selectivity
    print(results_by_db)




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
    datasets = getAllValues(json_result, "dataset")
    for dataset in datasets:
        #visualize_write_percentages(json_result, 1000)
        visualize_read_only(json_result, 13192591, 1000)


