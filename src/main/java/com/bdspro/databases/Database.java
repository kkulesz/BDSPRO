package com.bdspro.databases;

import com.bdspro.datasets.Dataset;
import com.bdspro.query.QueryTranslator;

public interface Database {

    int setup(Dataset dataset, boolean cluster);

    int load(String csvFile, Dataset dataset);

    int cleanup(String datasetTableName);

    int runStatement(String stmtString);

    int runQuery(String queryString);

    long getSize(String datasetTableName);

    int getRowCount(String datasetTableName);

    QueryTranslator getQueryTranslator();
}
