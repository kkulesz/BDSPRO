

public class KDBPlus implements Database {


    @Override
    public int setup(Dataset dataset) {
        return 0;
    }

    @Override
    public int load(String csvFile) {
        return 0;
    }

    @Override
    public int cleanup() {
        return 0;
    }

    @Override
    public int runStatement(String stmtString) {
        return 0;
    }

    @Override
    public int runQuery(String queryString) {
        return 0;
    }

    @Override
    public QueryTranslator getQueryTranslator() {
        return null;
    }
}
