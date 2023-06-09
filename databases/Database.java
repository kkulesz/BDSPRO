public interface Database {

    public int setup(Dataset dataset);

    public int load(String csvFile);

    public int cleanup();

    public int runStatement(String stmtString);

    public int runQuery(String queryString);

    public QueryTranslator getQueryTranslator();
}
