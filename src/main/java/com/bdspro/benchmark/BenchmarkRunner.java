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
        var batchSizes = new int[] {1, 10};
        var writePercentages = new int[] {25, 50};
        var writeFrequencies = new int[] {1, 100};
        var numberOfQueries = new int[] {1, 10};
        var numberOfNodes = new int[] {1, 3};
        var databases = new Database[]{
                //new TimescaleDb(),
                new ClickHouse()
        };

        // TODO: it is so stupid and unreadable that i will change it for sure. It is just cartesian product of those arrays, right?
        for (var wp : writePercentages)
            for( var wf :writeFrequencies )
                for (var non: numberOfNodes)
                    for (var ds : datasets)
                        for (var noq: numberOfQueries)
                            for (var bs: batchSizes) {
                                var msg = String.format(
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
                                //b.run();
                            }
    }

    public static void main(String[] args) {
        var br = new BenchmarkRunner();
        br.run();
    }
}
