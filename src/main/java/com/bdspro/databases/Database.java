package com.bdspro.databases;

import com.bdspro.datasets.Dataset;
import com.bdspro.query.QueryTranslator;

public interface Database {

    public int setup(Dataset dataset);

    public int load(String csvFile, String databaseTableName);

    public int cleanup(String datasetTableName);

    public int runStatement(String stmtString);

    public int runQuery(String queryString);

    public QueryTranslator getQueryTranslator();
}
