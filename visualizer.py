import json
from statistics import mean
import matplotlib.pyplot as plt
import numpy
import os
import numpy as np

RESULTS_DIR = "results"
RUN_DIR = "run8-multiNode-writeOnly"
RESULTS_FILE = "benchmark_result"
PLOTS_DIR_NAME = "plots"

RESULT_FILE_PATH = os.path.join(RESULTS_DIR, RUN_DIR, RESULTS_FILE)
PLOTS_DIR_PATH = os.path.join(RESULTS_DIR, RUN_DIR, PLOTS_DIR_NAME)


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
        return 0, mean(write_results), 0
    if (write_results == []):
        return mean(read_results), 0, 0
    return mean(read_results), mean(write_results), mean(read_results + write_results)


## returns list of all values found in the json for the parameter 'value_name'
def getAllValues(json, value_name):
    values = []
    for benchmark in json:
        values.append(benchmark[value_name])
    values = sorted(set(values))
    return values


## returns list of all Databases used in the benchmark
def getAllDBs(json):
    return sorted(list(json[0]['readResults'].keys()))


def filter_json(json, value_name, value):
    res = []
    for benchmark in json:
        if benchmark[value_name] == value:
            res.append(benchmark)
    return res


## displays a multiple bar chart with the latency results on the y axis and the writePercentage on the x axis
def visualize_write_percentages(json, batch_size):
    json = filter_json(json, "batchSize", batch_size)
    values = getAllValues(json, "writePercentage")
    values = sorted(values)
    dbs = getAllDBs(json)
    results_by_db = {}
    for db in dbs:
        means = []
        for wp in values:
            # TODO: write values are ignored right now
            r, w, m = getAveragesForDBAndValue(json, db, "writePercentage", wp)
            if m == 0:
                means.append(max(r, w) / 1000000.0)
            else:
                means.append(m / 1000000.0)
        results_by_db[db] = means
    x = numpy.array(values)
    ax = plt.subplot(111)
    ax.bar(x - 1, results_by_db[dbs[0]], width=2, color='b', align='center', label=dbs[0])
    ax.bar(x + 1, results_by_db[dbs[1]], width=2, color='r', align='center', label=dbs[1])
    plt.legend(loc="upper right")
    plt.xlabel('Write Percentage')
    plt.ylabel('Average Query Latency in ms')
    plt.savefig(os.path.join(PLOTS_DIR_PATH, "avg-que-latency-writes"))
    plt.show()


def visualize_read_only(json, row_count, batch_size):
    json = filter_json(json, "batchSize", batch_size)
    dbs = getAllDBs(json)
    results_by_db = {}
    # buckets = [0, 1, 0.0001 * row_count, 0.001 * row_count, 0.01 * row_count, 0.1 * row_count, row_count]
    buckets = [1, 0.01 * row_count, 0.05 * row_count, 0.1 * row_count, 0.15 * row_count, 0.2 * row_count, row_count]
    for db in dbs:
        latencies_by_selectivity = {}
        for bucket in buckets:
            latencies_by_selectivity[str(bucket)] = []
        for benchmark in json:
            res = benchmark["readResults"][db]
            for queryRes in res:
                for i in range(len(buckets)):
                    if queryRes["rowCount"] <= buckets[i] or i == len(buckets) - 1:
                        latencies_by_selectivity[str(buckets[i])].append(queryRes["latency"])
                        break
        latencies = []
        for key in latencies_by_selectivity.keys():
            if len(latencies_by_selectivity[key]) == 0:
                latencies.append(0)
                continue
            latencies.append(mean(latencies_by_selectivity[key]) / 1000000.0)
        results_by_db[db] = latencies
    x = numpy.array(range(len(buckets)))  # numpy.array(buckets)
    ax = plt.subplot(111)
    # plt.xticks(ticks=range(len(x)), labels=["0", "1", "<0.01%", "<0.1%", "<1%", "<10%", ">=10%"], rotation=45)
    plt.xticks(ticks=range(len(x)), labels=["1", "<1%", "<5%", "<10%", "<15%", "<20%", ">=25%"], rotation=45)
    ax.bar(x - 0.1, results_by_db[dbs[0]], width=.2, color='b', align='center', label=dbs[0])
    ax.bar(x + 0.1, results_by_db[dbs[1]], width=.2, color='r', align='center', label=dbs[1])
    plt.legend(loc="upper left")
    plt.xlabel('Selectivity')
    plt.ylabel('Average Latency in ms')
    plt.tight_layout()
    plt.savefig(os.path.join(PLOTS_DIR_PATH, "avg-que-latency-selectivity"))
    plt.show()


def getAllLatencies(json, db):
    latencies = []
    for benchmark in json:
        for res in benchmark["readResults"][db]:
            latencies.append(res["latency"])
    return latencies


def getLatenciesForDBAndQueryType(json, db, type):
    latencies = []
    for benchmark in json:
        for res in benchmark["readResults"][db]:
            if res["queryType"] == type:
                latencies.append(res["latency"])
    return latencies


def showLatencies(json, db):
    lat = getAllLatencies(json, db)
    lat = sorted(lat)
    x = range(len(lat))
    plt.plot(lat)
    plt.savefig(os.path.join(PLOTS_DIR_PATH, f"latencies-{db}"))
    plt.show()


def groupByQueryType(json):
    plt.rcParams["figure.figsize"] = (10, 10)
    results_by_db = {}
    dbs = getAllDBs(json)
    qTypes = []
    for res in json[1]["readResults"][dbs[0]]:
        qTypes.append(res["queryType"])
    for db in dbs:
        results_by_qType = {}
        for type in qTypes:
            results_by_qType[type] = mean(getLatenciesForDBAndQueryType(json, db, type)) / 1000000.0
        results_by_db[db] = results_by_qType

    ts_results = list(sorted(list(results_by_db["TimescaleDb"].items())))
    ch_results = list(sorted(list(results_by_db["ClickHouse"].items())))
    x = list(zip(*ts_results))[0]
    y_ts = list(zip(*ts_results))[1]
    y_ch = list(zip(*ch_results))[1]

    plt.xticks(ticks=range(len(x)), labels=x, rotation=90)
    plt.plot(x, y_ch, color='blue', label='Clickhouse')
    plt.plot(x, y_ts, color='red', label='TimescaleDB')
    plt.legend(loc="upper left")
    plt.xlabel('Query Type')
    plt.ylabel('Average Latency in ms')
    plt.title("Group by Query Type")
    plt.gcf().subplots_adjust(bottom=.35)
    # plt.tight_layout()
    plt.savefig(os.path.join(PLOTS_DIR_PATH, "avg-que-latency-query-type"))
    plt.show()
    plt.rcParams["figure.figsize"] = plt.rcParamsDefault["figure.figsize"]


def visualize_write_only(json, dataset):
    json = list(filter(lambda j: j["dataset"] == dataset, json))
    values = sorted(getAllValues(json, "batchSize"))
    dbs = getAllDBs(json)
    results_by_db = {}
    for db in dbs:
        means = []
        for bs in values:
            _, w, _ = getAveragesForDBAndValue(json, db, "batchSize", bs)
            means.append(w / 1000000.0)
        results_by_db[db] = means
    x = numpy.array(values)
    plt.xscale("log")
    plt.plot(x, results_by_db[dbs[0]], color='b', label=dbs[0])
    plt.plot(x, results_by_db[dbs[1]], color='r', label=dbs[1])
    plt.legend(loc="upper left")
    plt.xlabel('Batch size')
    plt.ylabel('Average write latency in ms')
    plt.tight_layout()
    plt.savefig(os.path.join(PLOTS_DIR_PATH, f"avg-latency-batch-size-{dataset}"))
    plt.show()


def visualize_compression_rate(json):
    compression_rates = json[0]["compressionRates"]
    plt.bar("TimescaleDb", compression_rates["TimescaleDb"], color='r')
    plt.bar("ClickHouse", compression_rates["ClickHouse"], color='b')
    plt.savefig(os.path.join(PLOTS_DIR_PATH, f"compression-rate"))
    plt.ylabel('Compression rate')
    plt.show()


def main():
    if not os.path.exists(PLOTS_DIR_PATH):
        os.makedirs(PLOTS_DIR_PATH)
    with open(RESULT_FILE_PATH) as json_file:
        json_result = json.load(json_file)

        # datasets = getAllValues(json_result, "dataset")
        # for dataset in datasets:
        ###### RUN1, RUN2, RUN5, RUN6 and RUN7
        # showLatencies(json_result, "ClickHouse")
        # showLatencies(json_result, "TimescaleDb")
        # visualize_write_percentages(json_result, 100)
        # visualize_read_only(json_result, 13192591, 100)
        # groupByQueryType(json_result)

        ###### RUN3 and RUN8
        visualize_write_only(json_result, "TaxiRidesDataset")
        visualize_write_only(json_result, "ClimateDataset")

        ###### RUN4
        # visualize_compression_rate(json_result)


if __name__ == "__main__":
    main()
