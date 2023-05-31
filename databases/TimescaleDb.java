package databases;

import datasets.TestDataset;
import query.QueryTranslator;
import query.SqlQueryTranslator;

import java.sql.Timestamp;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class TimescaleDb implements Database{
    private String connUrl = "jdbc:postgresql://localhost:5432/bdspro?user=postgres&password=123qweasdzx";
    private QueryTranslator queryTranslator = new SqlQueryTranslator();

    @Override
    public int setup() {
        var dataset = new TestDataset();
        try (var conn = DriverManager.getConnection(connUrl)) {
            var stmt = conn.createStatement();
            stmt.execute(queryTranslator.translateCreateTable(dataset));
            stmt.execute(String.format("SELECT create_hypertable('%s', '%s', if_not_exists => TRUE)", dataset.getTableName(), dataset.getTimeSeriesColumnName()));
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
            return 1;
        }

        return 0;
    }

    @Override
    public int load(String csvFile) {
        return 0;
    }

    @Override
    public int cleanup() {
        return 0;
    }

    @Override
    public int insert(Object data, Timestamp timestamp) {
        var dataset = new TestDataset();
        try (var conn = DriverManager.getConnection(connUrl)) {
            var stmt = conn.createStatement();
            var list = new ArrayList<String>();
            list.add("01.01.2015");
            list.add("16010072.1195");
            list.add("12729");
            list.add("1257.76354148008");
            list.add("30024676");
            System.out.println(queryTranslator.translateInsertInto(dataset, list));
//            stmt.executeUpdate(queryTranslator.translateInsertInto(dataset, list));
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
            return 1;
        }
        return 0;
    }

    @Override
    public int read(String query) {
        return 0;
    }
}
