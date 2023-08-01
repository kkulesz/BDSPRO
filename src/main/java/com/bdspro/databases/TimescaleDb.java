package com.bdspro.databases;

import com.bdspro.datasets.Dataset;
import com.bdspro.query.QueryTranslator;
import com.bdspro.query.sql.SqlQueryTranslator;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class TimescaleDb implements Database {
    private final String connUrl = "jdbc:postgresql://timescaledb:5432/bdspro?user=timescaledb&password=password";
    private final QueryTranslator queryTranslator = new SqlQueryTranslator();


    @Override
    public int setup(Dataset dataset) {
        runStatement(queryTranslator.translateCreateTable(dataset));
        String crtTableStmt = String.format(
                "SELECT create_hypertable('%s', '%s', if_not_exists => TRUE)",
                dataset.getTableName(), dataset.getTimeStampColumnName().toLowerCase()
        );
        return runQuery(crtTableStmt);
    }

    @Override
    public int load(String csvFile, Dataset dataset) {
        try {
            CopyManager copyManager = new CopyManager((BaseConnection) DriverManager.getConnection(connUrl));
            FileReader reader = new FileReader(csvFile);
            copyManager.copyIn("COPY " + dataset.getTableName() + " FROM STDIN (DELIMITER(','))", reader );
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public int cleanup(String datasetTableName) {
        String stmt = String.format("DROP TABLE %s", datasetTableName);
        return runStatement(stmt);
    }

    @Override
    public int runStatement(String stmtString) {
        try (Connection conn = DriverManager.getConnection(connUrl)) {
            PreparedStatement stmt = conn.prepareStatement(stmtString);
            stmt.executeUpdate();
            return 0;
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
            return -1;
        }
    }

    @Override
    public int runQuery(String queryString) {
        try (Connection conn = DriverManager.getConnection(connUrl)) {
            PreparedStatement stmt = conn.prepareStatement(queryString);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
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
        try (Connection conn = DriverManager.getConnection(connUrl)) {
            String queryString = String.format("SELECT hypertable_size('%s')", datasetTableName);
            PreparedStatement stmt = conn.prepareStatement(queryString);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
            return -1;
        }
    }

    @Override
    public int getRowCount(String datasetTableName) {
        try (Connection conn = DriverManager.getConnection(connUrl)) {
            PreparedStatement stmt = conn.prepareStatement(queryTranslator.translateSelectCount(datasetTableName));
            ResultSet rs = stmt.executeQuery();
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

}
