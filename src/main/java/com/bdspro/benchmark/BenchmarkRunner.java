package com.bdspro.benchmark;

import com.bdspro.databases.ClickHouse;
import com.bdspro.databases.Database;
import com.bdspro.databases.TimescaleDb;
import com.bdspro.datasets.Dataset;
import com.bdspro.datasets.TestDataset;

public class BenchmarkRunner {

    public void run(){
        // TODO: read file with configuration?

        // !Careful with the number of parameters below, number of runs will be equal to p1*p2*p3...
        var datasets = new Dataset[] {
                new TestDataset()
        };
        var batch_sizes = new int[] {1, 10};
        var write_percentages = new int[] {25, 50};
        var write_frequencies = new int[] {1, 100};
        var number_of_queries = new int[] {1, 10};
        var number_of_nodes = new int[] {1, 3};
        var databases = new Database[]{
                new TimescaleDb(),
//                new ClickHouse()
        };

        // TODO: it is so stupid and unreadable that i will change it for sure. It is just cartesian product of those arrays, right?
        for (var wp : write_percentages)
            for( var wf :write_frequencies )
                for (var non: number_of_nodes)
                    for (var ds : datasets)
                        for (var noq: number_of_queries)
                            for (var bs: batch_sizes) {
                                var msg = String.format(
                                        "Running benchmark with configuration:\n" +
                                                "\twrite_percentage=%s\n" +
                                                "\twrite_frequency=%s\n" +
                                                "\tnumber_of_nodes=%s\n" +
                                                "\tdataset=%s\n" +
                                                "\tnumber_of_queries=%s\n" +
                                                "\tbatch_size=%s\n",
                                        wp, wf, non, ds.getTableName(), noq, bs
                                );
                                System.out.println(msg);
//                                (new Benchmark(wp, wf, databases, non, ds, noq, bs)).run();
                            }
    }

    public static void main(String[] args) {
        var br = new BenchmarkRunner();
        br.run();
    }
}
