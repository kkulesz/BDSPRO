package com.bdspro.test;

import com.bdspro.benchmark.Benchmark;
import com.bdspro.databases.Database;
import com.bdspro.databases.TimescaleDb;
import com.bdspro.datasets.TaxiRidesDataset;
import com.bdspro.datasets.TestDataset;

import java.sql.SQLException;

public class TestMain {

    public static void main(String... args) throws SQLException{
        var ds = new TestDataset();

        var dbs = new Database[]{
                new TimescaleDb()
        };

        var benchmark = new Benchmark(50, 50, dbs, 1, ds,10, 10);
        benchmark.run();
    }
}