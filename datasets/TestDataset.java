package datasets;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestDataset implements Dataset{
    @Override
    public String getTableName() {
        return "test_table";
    }

    @Override
    public List<Map.Entry<String, ColumnType>> getColumnNamesWithTypes() {
        var list = new ArrayList<Map.Entry<String, ColumnType>>(){};

        list.add(new AbstractMap.SimpleEntry<>("Period", ColumnType.TIMESTAMP));
        list.add(new AbstractMap.SimpleEntry<>("Revenue", ColumnType.NUMERIC));
        list.add(new AbstractMap.SimpleEntry<>("Sales_quantity", ColumnType.NUMERIC));
        list.add(new AbstractMap.SimpleEntry<>("Average_cost", ColumnType.NUMERIC));
        list.add(new AbstractMap.SimpleEntry<>("The_average_annual_payroll_of_the_region", ColumnType.NUMERIC));

        return list;
    }

    @Override
    public String getCsvName() {
        return "TestData/test.csv";
    }

    @Override
    public String getTimeStampColumnName() {
        return "period";
    }
}
