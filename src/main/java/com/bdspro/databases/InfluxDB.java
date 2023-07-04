package com.bdspro.databases;

import com.bdspro.datasets.Dataset;
import com.bdspro.query.QueryTranslator;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

import java.util.List;

public class InfluxDB implements Database {

    private final String token;
    private final String bucket;
    private final String org;
    private final String url;
    private InfluxDBClient client;

    public InfluxDB(String token, String bucket, String org, String url) {
        this.token = token;
        this.bucket = bucket;
        this.org = org;
        this.url = url;
        client = buildConnection();
    }

    @Override
    public int setup(Dataset dataset) {
        //TODO: create table according to dataset scheme
        return 0;
    }

    @Override
    public int load(String csvFile, Dataset dataset) {
        //TODO: load csv file into database
        return 0;
    }

    @Override
    public int cleanup(String datasetTableName) {
        //TODO: drop table
        client.close();
        return 0;
    }

    @Override
    public int runStatement(String stmtString) {
        try (WriteApi writeApi = client.getWriteApi()) {
            writeApi.writeRecord(bucket, org, WritePrecision.NS, stmtString);
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int runQuery(String queryString) {
        QueryApi queryApi = client.getQueryApi();
        int count = 0;

        List<FluxTable> tables = queryApi.query(queryString);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                count++;
                System.out.println(fluxRecord.getValue());
            }
        }
        return count;
    }

    @Override
    public int getSize(String datasetTableName) {
        //TODO
        return 0;
    }

    @Override
    public int getRowCount(String datasetTableName) {
        //TODO
        return 0;
    }

    @Override
    public QueryTranslator getQueryTranslator() {
        //TODO: implement class FluxQueryTranslator and return an instance here
        return null;
    }

    private InfluxDBClient buildConnection() {
        return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }

    public static void main(String[] args) {
        InfluxDB db = new InfluxDB("MKki9vTYAUxdy4jjTl76KXnWZb5PhbtWKarfHXtBTChQerD0wcvdTHqRvRbizYiQdjo7Zlt4DlcCA7S6IbKL1g==", "test", "test", "http://localhost:8086");
        db.runStatement("mem,host=host1 used_percent=23.43234543");
        String query = "from(bucket: \"" + db.bucket + "\") |> range(start: -1h)";
        db.runQuery(query);
    }

}
