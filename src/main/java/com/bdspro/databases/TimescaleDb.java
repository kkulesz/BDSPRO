package com.bdspro.databases;

import com.bdspro.datasets.Dataset;
import com.bdspro.datasets.TaxiRidesDataset;
import com.bdspro.query.QueryTranslator;
import com.bdspro.query.sql.SqlQueryTranslator;

import java.io.File;
import java.sql.DriverManager;

public class TimescaleDb implements Database {
    private String connUrl = "jdbc:postgresql://localhost:5432/bdspro?user=timescaledb&password=password";
    private QueryTranslator queryTranslator = new SqlQueryTranslator();


    @Override
    public int setup(Dataset dataset) {
        runStatement(queryTranslator.translateCreateTable(dataset));
        var crtTableStmt = String.format(
                "SELECT create_hypertable('%s', '%s', if_not_exists => TRUE)",
                dataset.getTableName(), dataset.getTimeStampColumnName().toLowerCase()
        );
        return runQuery(crtTableStmt);
    }

    @Override
    public int load(String csvFile, Dataset dataset) {
        File f = new File(csvFile);
        var stmt = String.format(
              "COPY %s FROM '%s' delimiter ',' CSV HEADER", dataset.getTableName(), f.getAbsolutePath()
            );
        return runStatement(stmt);
    }

    @Override
    public int cleanup(String datasetTableName) {
        var stmt = String.format("DROP TABLE %s", datasetTableName);
        return runStatement(stmt);
    }

    @Override
    public int runStatement(String stmtString) {
        try (var conn = DriverManager.getConnection(connUrl)) {
            var stmt = conn.prepareStatement(stmtString);
            stmt.executeUpdate();
            return 0;
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
            return -1;
        }
    }

    @Override
    public int runQuery(String queryString) {
        try (var conn = DriverManager.getConnection(connUrl)) {
            var stmt = conn.prepareStatement(queryString);
            var rs = stmt.executeQuery();

            var count = 0;
            while(rs.next()) {
                count++;
            }

            return count;
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
            return -1;
        }
    }

    @Override
    public int getSize(String datasetTableName) {
        try (var conn = DriverManager.getConnection(connUrl)) {
            var queryString = String.format("SELECT hypertable_size('%s')", datasetTableName);
            var stmt = conn.prepareStatement(queryString);
            var rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
            return -1;
        }
    }

    @Override
    public int getRowCount(String datasetTableName) {
        try (var conn = DriverManager.getConnection(connUrl)) {
            var stmt = conn.prepareStatement(queryTranslator.translateSelectCount(datasetTableName));
            var rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
            return -1;
        }
    }

    @Override
    public QueryTranslator getQueryTranslator() {
        return queryTranslator;
    }

    public static void main(String[] args) {
        Dataset testdata = new TaxiRidesDataset();
        TimescaleDb db = new TimescaleDb();
        db.setup(testdata);
        db.load(testdata.getCsvName(), testdata);
        db.runQuery("SELECT * FROM " + testdata.getTableName() + ";");
//        db.cleanup(testdata.getTableName());
    }

}
