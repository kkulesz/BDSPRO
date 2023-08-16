package com.bdspro.databases;

import com.bdspro.datasets.ColumnType;
import com.bdspro.datasets.Dataset;
import com.bdspro.query.QueryTranslator;
import com.bdspro.query.sql.SqlQueryTranslator;
import com.clickhouse.client.*;
import com.clickhouse.client.config.ClickHouseClientOption;
import com.clickhouse.data.ClickHouseFile;
import com.clickhouse.data.ClickHouseFormat;
import com.clickhouse.data.ClickHouseRecord;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
public class ClickHouse implements Database {

    ClickHouseNode server;
    private final QueryTranslator queryTranslator = new SqlQueryTranslator();

    private boolean cluster = false;

    @Override
    public int setup(Dataset dataset, boolean cluster) {
        this.cluster = cluster;
        server = getServer(1);
        createTable(dataset);


        if (cluster) {
            //setup table on all servers
            server = getServer(2);
            createTable(dataset);
            server = getServer(3);
            createTable(dataset);
            server = getServer(1);
            //setup distributed table view on node one as table_name_dist with distributed engine and timestamp as shard key
            try (ClickHouseClient client = ClickHouseClient.newInstance(server.getProtocol())) {
                ClickHouseRequest<?> request = client.connect(server);
                StringBuilder schema = new StringBuilder();
                for (Map.Entry<String, ColumnType> column :dataset.getColumnNamesWithTypes()) {
                    schema.append(column.getKey()).append(" ").append(columnTypeToString(column.getValue())).append(", ");
                }
                schema.delete(schema.length() - 2, schema.length());
                String query = "create table if not exists benchmark." + dataset.getTableName() + "(" + schema + ") engine=Distributed(benchmark_cluster, benchmark, " + dataset.getTableName() + "_node, toYYYYMMDD(" + dataset.getTimeStampColumnName() + "));";
                request.query(query).execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private void createTable(Dataset dataset)  {

        String tablename = cluster ? dataset.getTableName() + "_node" : dataset.getTableName();
        try (ClickHouseClient client = ClickHouseClient.newInstance(server.getProtocol())) {
            ClickHouseRequest<?> request = client.connect(server);
            StringBuilder schema = new StringBuilder();
            for (Map.Entry<String, ColumnType> column :dataset.getColumnNamesWithTypes()) {
                schema.append(column.getKey()).append(" ").append(columnTypeToString(column.getValue())).append(", ");
            }
            schema.delete(schema.length() - 2, schema.length());
            String query = "create table if not exists " + tablename + "(" + schema + ") engine=MergeTree() order by " + dataset.getTimeStampColumnName() + ";";
            request.query(query).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ClickHouseNode getServer(int i) {
        return ClickHouseNode.builder()
                .host(System.getProperty("chHost", getHost(i)))
                .port(ClickHouseProtocol.HTTP, Integer.getInteger("chPort", getPort()))
                .database("benchmark").credentials(ClickHouseCredentials.fromUserAndPassword(
                        System.getProperty("chUser", "bdspro"), System.getProperty("chPassword", "password")))
                .addOption(ClickHouseClientOption.SOCKET_TIMEOUT.getKey(), "600000") // 20x bigger than default
                .build();
    }

    private int getPort() {
        return cluster ? 8124 : 8123;
    }

    private String getHost(int i) {
        if (cluster) {
            return "cloud-4" + i;
        }
        return "clickhouse-" + i;
    }

    @Override
    public int load(String csvFile, Dataset dataset) {
        ClickHouseFile file = ClickHouseFile.of(csvFile);

        try (ClickHouseClient client = ClickHouseClient.newInstance(server.getProtocol())) {
            client.write( server )
                    .set( "input_format_csv_skip_first_lines", "1" )
                    .set( "format_csv_delimiter", "," )
                    .table( dataset.getTableName() )
                    .data( file )
                    .executeAndWait();
        } catch (ClickHouseException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;

        }

    @Override
    public int cleanup(String datasetTableName) {
        try (ClickHouseClient client = ClickHouseClient.newInstance(server.getProtocol())) {
            ClickHouseRequest<?> request = client.connect(server);
            String query = "drop table " + datasetTableName + ";";
            request.query(query).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    @Override
    public int runStatement(String stmtString) {

        try (ClickHouseClient client = ClickHouseClient.newInstance(server.getProtocol());
             ClickHouseResponse response = client.write(server)
                     .format(ClickHouseFormat.CSV)
                     .query(stmtString).execute().get()) {
            return 0;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
            return -1;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int runQuery(String queryString) {
        try (ClickHouseClient client = ClickHouseClient.newInstance(server.getProtocol());
             ClickHouseResponse response = client.read(server)
                     .format(ClickHouseFormat.CSV)
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
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public long getSize(String datasetTableName) {
        String query = "SELECT formatReadableSize(sum(data_compressed_bytes) AS size) AS compressed FROM system.parts" +
                " WHERE (active = 1) AND (database='benchmark') AND (table='" + datasetTableName + "');";
        try (ClickHouseClient client = ClickHouseClient.newInstance(server.getProtocol());
             ClickHouseResponse response = client.read(server)
                     .format(ClickHouseFormat.CSV)
                     .query(query).execute().get()) {
            int count = 0;
            for (ClickHouseRecord r : response.records()) {
                int multiplier = getMultiplier(r.getValue(0).asString().split(" ")[1].split("\"")[0]);
                return multiplier * Long.parseLong(r.getValue(0).asString().split("\\.")[0].substring(1));
            }
            return count;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
            return -1;
        } catch (ExecutionException | NoSuchElementException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int getRowCount(String datasetTableName) {
        try (ClickHouseClient client = ClickHouseClient.newInstance(server.getProtocol());
             ClickHouseResponse response = client.read(server)
                     .format(ClickHouseFormat.CSV)
                     .query(queryTranslator.translateSelectCount(datasetTableName))
                     .execute()
                     .get()
        ) {
            return response.firstRecord().getValue(0).asInteger(); // TODO: check whether it works
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        } catch (ExecutionException e) {
            return -1;
        }
    }

    private int getMultiplier(String unit) {
        switch (unit) {
            case "MiB" -> {
                return 1048576;
            }
            case "KiB" -> {
                return 1024;
            }
            case "GiB" -> {
                return 1073741824;
            }
        }
        throw new NoSuchElementException("Multiplier " + unit + " not known yet");
    }

    @Override
    public QueryTranslator getQueryTranslator() {
        return queryTranslator;
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
