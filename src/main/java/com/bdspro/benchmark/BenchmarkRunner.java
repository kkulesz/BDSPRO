package com.bdspro.benchmark;

import com.bdspro.databases.ClickHouse;
import com.bdspro.databases.Database;
import com.bdspro.databases.TimescaleDb;
import com.bdspro.datasets.ClimateDataset;
import com.bdspro.datasets.Dataset;
import com.bdspro.datasets.TestDataset;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkRunner {

    public String run(){
        // TODO: read file with configuration?

        // !Careful with the number of parameters below, number of runs will be equal to p1*p2*p3...
        var datasets = new Dataset[] {
                new ClimateDataset()
        };
        var batchSizes = new int[] {1000};
        var writePercentages = new int[] {0, 25, 50, 75, 100};
        var writeFrequencies = new int[] {100};
        var numberOfQueries = new int[] {100};
        var numberOfNodes = new int[] {1};
        var databases = new Database[]{
                //new TimescaleDb(),
                new ClickHouse()
        };
        StringBuilder resultJson = new StringBuilder();
        for (var wp : writePercentages)
            for( var wf :writeFrequencies )
                for (var non: numberOfNodes)
                    for (var ds : datasets)
                        for (var noq: numberOfQueries)
                            for (var bs: batchSizes) {
                                String msg = String.format(
                                        """
                                                Running benchmark with configuration:
                                                twrite_percentage=%s
                                                twrite_frequency=%s
                                                tnumber_of_nodes=%s
                                                tdataset=%s
                                                tnumber_of_queries=%s
                                                tbatch_size=%s
                                            """,
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
        var br = new BenchmarkRunner();
        String resultJson = br.run();
        String resultString = "[" + resultJson.toString() + "]";
        try (PrintWriter out = new PrintWriter("benchmark_result")) {
            out.println(resultString);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
