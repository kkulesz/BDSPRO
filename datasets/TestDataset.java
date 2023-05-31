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
    public List<Map.Entry<String, String>> getColumnNamesWithTypes() {
        var list = new ArrayList<Map.Entry<String, String>>(){};

        list.add(new AbstractMap.SimpleEntry<String, String>("Period", "TIMESTAMPTZ"));
        list.add(new AbstractMap.SimpleEntry<String, String>("Revenue", "NUMERIC"));
        list.add(new AbstractMap.SimpleEntry<String, String>("Sales_quantity", "INT"));
        list.add(new AbstractMap.SimpleEntry<String, String>("Average_cost", "NUMERIC"));
        list.add(new AbstractMap.SimpleEntry<String, String>("The_average_annual_payroll_of_the_region", "NUMERIC"));

        return list;
    }

    @Override
    public String getCsvName() {
        return "TestData/test.csv";
    }

    @Override
    public String getTimeSeriesColumnName() {
        return "period";
    }
}
