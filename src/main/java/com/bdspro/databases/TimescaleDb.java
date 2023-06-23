package com.bdspro.databases;

import com.bdspro.datasets.Dataset;
import com.bdspro.datasets.TaxiRidesDataset;
import com.bdspro.query.QueryTranslator;
import com.bdspro.query.sql.SqlQueryTranslator;

import java.sql.DriverManager;
import java.nio.file.Path;
import java.nio.file.Files;

public class TimescaleDb implements Database {
    private String connUrl = "jdbc:postgresql://localhost:5432/bdspro?user=postgres&password=123qweasdzx";
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
        // completely terrible performance of course. Will probably take ages to load few GBs of data.
        // TODO: either batch insert or just use timescaleDB functionality in docker
        try {
            Path path = Path.of(csvFile);
            Files.lines(path)
                    .skip(1)
                    .map(s -> s.split(","))
                    .map(values -> queryTranslator.translateInsertInto(dataset, values))
                    .forEach(this::runStatement);
        }catch(Exception e){
            System.out.println("Failed loading dataset!");
            return -1;
        }
        return 0;
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
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
            return 1;
        }
        return 0;
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
