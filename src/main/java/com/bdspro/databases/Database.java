package com.bdspro.databases;

import com.bdspro.datasets.Dataset;
import com.bdspro.query.QueryTranslator;

public interface Database {

    public int setup(Dataset dataset);

    public int load(String csvFile, Dataset dataset);

    public int cleanup(String datasetTableName);

    public int runStatement(String stmtString);

    public int runQuery(String queryString);

    public int getSize(String datasetTableName);

    public int getRowCount(String datasetTableName);

    public QueryTranslator getQueryTranslator();
}
