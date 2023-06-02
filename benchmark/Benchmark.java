package benchmark;

import databases.Database;
import datasets.Dataset;
import query.QueryTranslator;
import query.QueryType;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Benchmark {


    private int writePercentage;
    private Database database;
    private int numberOfNodes;
    private Dataset dataset;
    private QueryTranslator queryTranslator;

    public Benchmark(int writePercentage, Database database, int numberOfNodes, Dataset dataset) {
        this.writePercentage = writePercentage;
        this.database = database;
        this.numberOfNodes = numberOfNodes;
        this.dataset = dataset;

        this.database.setup(this.dataset);
        this.queryTranslator = database.getQueryTranslator();
    }

    public void run(){
        var queryString = queryTranslator.translate(QueryType.EXACT_POINT);

        var rowData = new ArrayList<String>();
        rowData.add("12.12.2017");
        rowData.add("16010072.1195");
        rowData.add("12729");
        rowData.add("1257.76354148008");
        rowData.add("30024676");
        var insertString = queryTranslator.translateInsertInto(this.dataset, rowData);

        var rangeQueryString = queryTranslator.translateRangeAnyEntity(this.dataset, Timestamp.valueOf("2014-11-12 01:02:03.123456789"), Timestamp.valueOf("2018-11-12 01:02:03.123456789"));
        var rangeQueryWithAggregationString = queryTranslator.translateRangeQueryWithAggregation(this.dataset, Timestamp.valueOf("2014-11-12 01:02:03.123456789"), Timestamp.valueOf("2018-11-12 01:02:03.123456789"));
        var pointQueryString = queryTranslator.translateExactPoint(this.dataset, Timestamp.valueOf("2017-12-12 00:00:00"));

        for (int i = 0; i<5; i++){

            this.database.runQuery(queryString);
            this.database.runQuery(rangeQueryString);
            this.database.runStatement(insertString);
            this.database.runQuery(rangeQueryWithAggregationString);
            this.database.runQuery(pointQueryString);
        }
        return;
    }

}
