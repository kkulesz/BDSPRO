import benchmark.Benchmark;
import databases.TimescaleDb;
import datasets.TestDataset;
import query.QueryType;
import query.SqlQueryTranslator;

import java.sql.DriverManager;
import java.sql.SQLException;

public class TestMain {

    public static void main(String... args) throws SQLException{
//        DriverManager.drivers().forEach(System.out::println);
//        System.out.println(System.getProperty("java.version"));

//        var connUrl = "jdbc:postgresql://localhost:5432/bdspro?user=postgres&password=123qweasdzx";
//        var conn = DriverManager.getConnection(connUrl);
//        System.out.println(conn.getClientInfo());

//        var dataset = new TestDataset();
//        var queryTranslator = new SqlQueryTranslator();
//        System.out.println(queryTranslator.translateCreateTable(dataset));

//        var db = new TimescaleDb();
//        db.setup();
//        db.insert(null, null);
        var ds = new TestDataset();
        var db = new TimescaleDb();
        var benchmark = new Benchmark(50, db, 0,ds);
        benchmark.run(QueryType.EXACT_POINT);
    }
}