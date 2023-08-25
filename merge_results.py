import os
import json


def _read_json_files(jsons_dir):
    jsons_files = os.listdir(jsons_dir)
    jsons = []
    for json_file_name in jsons_files:
        proper_path = os.path.join(jsons_dir, json_file_name)
        with open(proper_path, 'r') as j:
            jsons.append(json.load(j))
    return jsons


def _group_json_by_parameters(jsons):
    """
    takes list of json objects, groups them in respect of parameters:
        (writePercentage, writeFrequency, numberOfNodes, batchSize, dataset)
    and returns dict: {params: [jsons]}
    """
    params_to_json_dict = {}
    for j in jsons:
        j = j[0]
        params = (
            j["writePercentage"],
            j["writeFrequency"],
            j["numberOfNodes"],
            # j["datasetRows"],
            j["batchSize"],
            j["dataset"],
        )
        if params in params_to_json_dict:
            params_to_json_dict[params].append(j)
        else:
            params_to_json_dict[params] = [j]

    return params_to_json_dict


def merge_experiments_with_the_same_parameters(params_to_json_dict):
    """
    takes dictionary {params: [json]} and merges "readResults" and "writeResults" for json
        objects with the same set of parameters

    prints warnings if there is too many or too little number of experiments for given set of parameters
    """
    merged_jsons = []
    for params in params_to_json_dict:
        jsons = params_to_json_dict[params]

        if len(jsons) < 2:
            read_results = jsons[0]["readResults"]
            missing_db = "TimescaleDb" if "ClickHouse" in read_results else "ClickHouse"
            print(f"Missing ({missing_db}) experiment for params: {params}")
        elif len(jsons) > 2:
            print(f"Something is incorrect, too many experiments for set of params: {params}")
        else:
            merged_experiment = jsons[0]
            merged_experiment["readResults"] = jsons[0]["readResults"] | jsons[1]["readResults"]
            merged_experiment["writeResults"] = jsons[0]["writeResults"] | jsons[1]["writeResults"]

            if type(jsons[0]["compressionRates"]) == list:  # old version of file
                db_0 = list(jsons[0]["readResults"].keys())[0]
                db_1 = list(jsons[1]["readResults"].keys())[0]
                compression_rates = {
                    db_0: jsons[0]["compressionRates"][0],
                    db_1: jsons[1]["compressionRates"][0]
                }
            else:  # new version of file
                compression_rates = jsons[0]["compressionRates"] | jsons[1]["compressionRates"]
            merged_experiment["compressionRates"] = compression_rates

            merged_jsons.append(merged_experiment)

    return merged_jsons


def merge_json_files(jsons_dir):
    jsons = _read_json_files(jsons_dir)
    params_to_json_dict = _group_json_by_parameters(jsons)
    merged = merge_experiments_with_the_same_parameters(params_to_json_dict)

    return merged


def main():
    experiments_dir = "results"
    run_dir = "run8-multiNode-taxi-writeOnly"
    sub_experiments_dir = "results"
    results_path = os.path.join(experiments_dir, run_dir, sub_experiments_dir)

    merged_json = merge_json_files(results_path)

    with open(os.path.join(experiments_dir, run_dir, "benchmark_result"), "w") as final_file:
        json.dump(merged_json, final_file, indent=4)


if __name__ == "__main__":
    main()
