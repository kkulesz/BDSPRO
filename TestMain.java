import benchmark.Benchmark;
import databases.TimescaleDb;
import datasets.TestDataset;
import query.QueryType;

import java.sql.SQLException;

public class TestMain {

    public static void main(String... args) throws SQLException{
        var ds = new TestDataset();
        var db = new TimescaleDb();
        var benchmark = new Benchmark(50, db, 0,ds);
        benchmark.run();
    }
}