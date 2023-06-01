package databases;

import datasets.Dataset;
import datasets.TestDataset;
import query.QueryTranslator;
import query.SqlQueryTranslator;

import java.sql.Timestamp;
import java.sql.DriverManager;
import java.util.ArrayList;

public class TimescaleDb implements Database{
    private String connUrl = "jdbc:postgresql://localhost:5432/bdspro?user=postgres&password=123qweasdzx";
    private QueryTranslator queryTranslator = new SqlQueryTranslator();


    @Override
    public int setup(Dataset dataset) {
        try (var conn = DriverManager.getConnection(connUrl)) {
            var stmt = conn.createStatement();
            stmt.execute(queryTranslator.translateCreateTable(dataset));
            stmt.execute(String.format("SELECT create_hypertable('%s', '%s', if_not_exists => TRUE)", dataset.getTableName(), dataset.getTimeStampColumnName()));
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
    public int runStatement(String stmtString) {
        try (var conn = DriverManager.getConnection(connUrl)) {
            var stmt = conn.prepareStatement(stmtString);
            var rs = stmt.executeUpdate();
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
            return 1;
        }
        return 0;
    }

    @Override
    public int runQuery(String queryString) {
        try (var conn = DriverManager.getConnection(connUrl)) {
            var stmt = conn.prepareStatement(queryString);
            var rs = stmt.executeQuery();
            // we do not really care about the result
//            rs.next();
//            System.out.println(rs.getTimestamp(1));
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
            return 1;
        }
        return 0;
    }

    @Override
    public QueryTranslator getQueryTranslator() {
        return queryTranslator;
    }

//    @Override
//    public int load(String csvFile) {
//        return 0;
//    }
//
//    @Override
//    public int cleanup() {
//        return 0;
//    }
//
//    @Override
//    public int runStatement(String stmtString) {
//        try (var conn = DriverManager.getConnection(connUrl)) {
//            var stmt = conn.prepareStatement(stmtString);
////            stmt.executeUpdate(queryTranslator.translateInsertInto(dataset, list));
//        }catch (Exception ex) {
//            System.err.println(ex.getMessage());
//            return 1;
//        }
//        return 0;
//    }
//
//    @Override
//    public QueryTranslator getQueryTranslator() {
//        return this.queryTranslator;
//    }

}
