
import java.sql.SQLException;

public class TestMain {

    public static void main(String... args) throws SQLException{
        var ds = new TestDataset();
        var db = new TimescaleDb();
        var dbs = new Database[]{db};
        var benchmark = new Benchmark(50, 10, dbs, 10, ds,10 , 10);
        benchmark.run();
    }
}