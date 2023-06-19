package com.bdspro.databases;

import com.bdspro.datasets.ColumnType;
import com.bdspro.datasets.Dataset;
import com.bdspro.datasets.TestDataset;
import com.bdspro.query.QueryTranslator;
import com.clickhouse.client.*;
import com.clickhouse.data.ClickHouseFile;
import com.clickhouse.data.ClickHouseFormat;
import com.clickhouse.data.ClickHouseRecord;

import java.util.Map;
import java.util.concurrent.ExecutionException;
public class ClickHouse implements Database {

    ClickHouseNode server;

    private Dataset dataset;
    @Override
    public int setup(Dataset dataset) {
        this.dataset = dataset;
        server = ClickHouseNode.builder()
                .host(System.getProperty("chHost", "localhost"))
                .port(ClickHouseProtocol.HTTP, Integer.getInteger("chPort", 8123))
                .database("benchmark").credentials(ClickHouseCredentials.fromUserAndPassword(
                        System.getProperty("chUser", "default"), System.getProperty("chPassword", "InYourFace")))
                .build();

        try (ClickHouseClient client = ClickHouseClient.newInstance(server.getProtocol())) {
            ClickHouseRequest<?> request = client.connect(server);
            StringBuilder schema = new StringBuilder();
            for (Map.Entry<String, ColumnType> column :dataset.getColumnNamesWithTypes()) {
                schema.append(column.getKey() + " " + columnTypeToString(column.getValue()) + ", ");
            }
            schema.delete(schema.length() - 2, schema.length());
            String query = "create table " + dataset.getTableName() + "(" + schema.toString() + ") engine=MergeTree() order by " + dataset.getTimeStampColumnName() + ";";
            System.out.println(query);
            request.query(query).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int load(String csvFile) {
        ClickHouseFile file = ClickHouseFile.of(csvFile);

        try (ClickHouseClient client = ClickHouseClient.newInstance(server.getProtocol())) {
            client.write( server )
                    .set( "input_format_csv_skip_first_lines", "1" )
                    .set( "format_csv_delimiter", "," )
                    .table( dataset.getTableName() )
                    .data( file )
                    .executeAndWait();
        } catch (ClickHouseException e) {
            return -1;
        }
        return 0;

        }

    @Override
    public int cleanup() {
        try (ClickHouseClient client = ClickHouseClient.newInstance(server.getProtocol())) {
            ClickHouseRequest<?> request = client.connect(server);
            String query = "drop table " + dataset.getTableName() + ";";
            System.out.println(query);
            request.query(query).execute().get();
            return 0;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int runStatement(String stmtString) {
        return 0;
    }

    @Override
    public int runQuery(String queryString) {
        try (ClickHouseClient client = ClickHouseClient.newInstance(server.getProtocol());
             ClickHouseResponse response = client.read(server)
                     .format(ClickHouseFormat.RowBinaryWithNames)
                     .query(queryString).execute().get()) {
            int count = 0;
            for (ClickHouseRecord r : response.records()) {
                count++;
            }
            return count;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        } catch (ExecutionException e) {
            return -1;
        }
    }

    @Override
    public QueryTranslator getQueryTranslator() {
        return null;
    }

    public static void main(String[] args) {
        ClickHouse ch = new ClickHouse();
        ch.setup(new TestDataset());
        ch.runQuery("SELECT * FROM " + ch.dataset.getTableName() + ";");
        ch.cleanup();
    }

    public static String columnTypeToString(ColumnType type){
        switch (type) {
            case STRING -> {
                return "String";
            }
            case NUMERIC -> {
                return "Float64";
            }
            case TIMESTAMP -> {
                return "DateTime64(9, 'Europe/Berlin')";
            }
            default -> {
                return "";
            }
        }
    }
}
