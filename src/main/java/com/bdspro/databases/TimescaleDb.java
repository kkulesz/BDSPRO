package com.bdspro.databases;

import com.bdspro.datasets.Dataset;
import com.bdspro.query.QueryTranslator;
import com.bdspro.query.sql.SqlQueryTranslator;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.*;
import java.sql.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimescaleDb implements Database {
    // change HOST if access node on other machine (e.g. cloud-11)
    // localhost and timescaledb as host do not work
    private final String connUrl = "jdbc:postgresql://cloud-41:5434/bdspro?user=timescaledb&password=password";
    private final QueryTranslator queryTranslator = new SqlQueryTranslator();


    @Override
    public int setup(Dataset dataset, boolean cluster) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        runStatement(queryTranslator.translateCreateTable(dataset));
        String crtTableStmt;
        if (cluster){
            List<Map.Entry<String, String>> listOfUniqueNodeValues = new ArrayList<>() {};
            listOfUniqueNodeValues.add(new AbstractMap.SimpleEntry<>("dn41", "cloud-41.dima.tu-berlin.de"));
            listOfUniqueNodeValues.add(new AbstractMap.SimpleEntry<>("dn42", "cloud-42.dima.tu-berlin.de"));
            listOfUniqueNodeValues.add(new AbstractMap.SimpleEntry<>("dn43", "cloud-43.dima.tu-berlin.de"));
//            listOfUniqueNodeValues.add(new AbstractMap.SimpleEntry<>("dn44", "cloud-44.dima.tu-berlin.de"));

            for (Map.Entry<String, String> t: listOfUniqueNodeValues) {
                System.out.println(String.format("Adding data node: %s", t.getValue()));
                String query = String.format(
                        "SELECT add_data_node('%s',  host =>'%s', port => '5433', password => 'password', if_not_exists => TRUE)",
                        t.getKey(), t.getValue()
                );
                runQuery(query);
            }

            crtTableStmt = String.format(
                    "SELECT create_distributed_hypertable('%s', '%s', if_not_exists => TRUE, migrate_data => TRUE)",
                    dataset.getTableName(), dataset.getTimeStampColumnName().toLowerCase()
            );
        }else{
            crtTableStmt = String.format(
                "SELECT create_hypertable('%s', '%s', if_not_exists => TRUE)",
                dataset.getTableName(), dataset.getTimeStampColumnName().toLowerCase()
        );
        }

        return runQuery(crtTableStmt);
    }

    @Override
    public int load(String csvFile, Dataset dataset) {
        try {
            CopyManager copyManager = new CopyManager((BaseConnection) DriverManager.getConnection(connUrl));
            FileReader reader = new FileReader(csvFile);
            while (reader.read() != '\n'){} // skip first line
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
    public long getSize(String datasetTableName) {
        try (Connection conn = DriverManager.getConnection(connUrl)) {
            String queryString = String.format("SELECT table_bytes FROM hypertable_detailed_size('%s')", datasetTableName);
            PreparedStatement stmt = conn.prepareStatement(queryString);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getLong(1);
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
