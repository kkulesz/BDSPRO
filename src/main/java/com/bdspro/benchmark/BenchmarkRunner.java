package com.bdspro.benchmark;

import com.bdspro.databases.ClickHouse;
import com.bdspro.databases.Database;
import com.bdspro.databases.TimescaleDb;
import com.bdspro.datasets.ClimateDataset;
import com.bdspro.datasets.Dataset;
import com.bdspro.datasets.TaxiRidesDataset;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class BenchmarkRunner {

    public String run(){
        // !Careful with the number of parameters below, number of runs will be equal to p1*p2*p3...
        Dataset[] datasets = new Dataset[] {
                new ClimateDataset(),
                new TaxiRidesDataset()
        };
        int[] batchSizes = new int[] {10000};
        int[] writePercentages = new int[] {0, 50, 100};
        int[] writeFrequencies = new int[] {0};
        int[] numberOfQueries = new int[] {1000};
        int[] numberOfNodes = new int[] {1};
        Database[] databases = new Database[]{
                new ClickHouse(),
                new TimescaleDb()
        };
        StringBuilder resultJson = new StringBuilder();
        for (int wp : writePercentages)
            for( int wf :writeFrequencies )
                for (int non: numberOfNodes)
                    for (Dataset ds : datasets)
                        for (int noq: numberOfQueries)
                            for (int bs: batchSizes) {
                                String msg = String.format(
                                        "Running benchmark with configuration: write_percentage=%s, write_frequency=%s, number_of_nodes=%s, dataset=%s, number_of_queries=%s, batch_size=%s",
                                        wp, wf, non, ds.getTableName(), noq, bs
                                );
                                System.out.println(msg);
                                Benchmark b = (new Benchmark(wp, wf, databases, non, ds, noq, bs));
                                b.run();
                                resultJson.append(b.getResultAsJSONString());
                                resultJson.append(",");

                            }
        resultJson.deleteCharAt(resultJson.length()-1);
        return resultJson.toString();
    }

    public static void main(String[] args) {
        BenchmarkRunner br = new BenchmarkRunner();
        String resultJson = br.run();
        String resultString = "[" + resultJson + "]";
        try (PrintWriter out = new PrintWriter("benchmark_result")) {
            out.println(resultString);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
