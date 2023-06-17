//import com.clickhouse.client.*;
//
//import java.util.Map;
//
//public class ClickHouse implements Database {
//
//    ClickHouseNode server;
//    @Override
//    public int setup(Dataset dataset) {
//        server = ClickHouseNode.builder()
//                .host(System.getProperty("chHost", "localhost"))
//                .port(ClickHouseProtocol.HTTP, Integer.getInteger("chPort", 8123))
//                .database("system").credentials(ClickHouseCredentials.fromUserAndPassword(
//                        System.getProperty("chUser", "benchmark"), System.getProperty("chPassword", "")))
//                .build();
//
//        try (ClickHouseClient client = ClickHouseClient.newInstance(server.getProtocol())) {
//            ClickHouseRequest<?> request = client.connect(server);
//            StringBuilder schema = new StringBuilder();
//            for (Map.Entry<String, ColumnType> column :dataset.getColumnNamesWithTypes()) {
//                schema.append(column.getKey() + " " + columnTypeToString(column.getValue()) + ", ");
//            }
//            schema.delete(schema.length() - 2, schema.length());
//            String query = "create table " + dataset.getTableName() + "(" + schema.toString() + ") engine=MergeTree() order by " + dataset.getTimeStampColumnName();
//            System.out.println(query);
//            request.query(query).execute().get();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//
//    @Override
//    public int load(String csvFile) {
//        return 0;
//    }
//
//    @Override
//    public int cleanup() {
//        return 0;
//    }
//
//    @Override
//    public int runStatement(String stmtString) {
//        return 0;
//    }
//
//    @Override
//    public int runQuery(String queryString) {
//        return 0;
//    }
//
//    @Override
//    public QueryTranslator getQueryTranslator() {
//        return null;
//    }
//
//    public static void main(String[] args) {
//        ClickHouse ch = new ClickHouse();
//        ch.setup(new TestDataset());
//    }
//
//    public static String columnTypeToString(ColumnType type){
//        switch (type) {
//            case STRING -> {
//                return "String";
//            }
//            case NUMERIC -> {
//                return "Float64";
//            }
//            case TIMESTAMP -> {
//                return "DateTime64(9, 'Europe/Berlin')";
//            }
//            default -> {
//                return "";
//            }
//        }
//    }
//}
